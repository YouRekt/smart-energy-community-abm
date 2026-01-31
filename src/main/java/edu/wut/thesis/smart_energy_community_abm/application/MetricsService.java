package edu.wut.thesis.smart_energy_community_abm.application;

import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    private final JdbcTemplate jdbcTemplate;

    private final BlockingQueue<Metric> queue = new LinkedBlockingQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread workerThread;

    @Value("${abm.database.batch-size:1000}")
    private int batchSize;

    @Value("${abm.database.flush-latency:1000}")
    private int maxLatencyMillis;

    @PostConstruct
    public void startWorker() {
        workerThread = new Thread(this::processQueue, "Global-Metrics-Writer");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @PreDestroy
    public void stopWorker() {
        running.set(false);
        if (workerThread != null) {
            workerThread.interrupt();
        }
        flushRemaining();
    }

    public void enqueue(@NonNull Metric metric) {
        if (!queue.offer(metric))
            log.error("Queue is full");
    }

    private void processQueue() {
        List<Metric> batch = new ArrayList<>(batchSize);

        long lastFlushTime = System.currentTimeMillis();

        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                Metric m = queue.poll(maxLatencyMillis, TimeUnit.MILLISECONDS);

                if (m != null) {
                    batch.add(m);
                    queue.drainTo(batch, batchSize - batch.size());
                }

                long currentTime = System.currentTimeMillis();
                boolean isFull = batch.size() >= batchSize;
                boolean isTimeUp = (currentTime - lastFlushTime) >= maxLatencyMillis;

                if (!batch.isEmpty() && (isFull || isTimeUp)) {
                    saveBatch(batch);
                    batch.clear();
                    lastFlushTime = currentTime;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in global metrics writer loop", e);
            }
        }
        flushRemaining();
    }

    private void saveBatch(List<Metric> batch) {
        if (batch.isEmpty()) return;

        String sql = """
                INSERT INTO metrics (id, time, name, value, timestamp)
                VALUES (nextval('metrics_id_seq'), ?, ?, ?, ?)
                """;

        try {
            jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, metric) -> {
                ps.setTimestamp(1, Timestamp.valueOf(metric.getTime()));
                ps.setString(2, metric.getName());
                ps.setDouble(3, metric.getValue());
                ps.setLong(4, metric.getTimestamp());
            });
        } catch (Exception e) {
            log.error("Failed to save JDBC batch", e);
        }
    }

    private void flushRemaining() {
        List<Metric> batch = new ArrayList<>();
        queue.drainTo(batch);
        saveBatch(batch);
    }
}