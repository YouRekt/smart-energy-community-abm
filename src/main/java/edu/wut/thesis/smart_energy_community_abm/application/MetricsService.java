package edu.wut.thesis.smart_energy_community_abm.application;

import edu.wut.thesis.smart_energy_community_abm.domain.timeseries.Metric;
import edu.wut.thesis.smart_energy_community_abm.persistence.MetricsRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    private final MetricsRepository metricsRepository;

    private final BlockingQueue<Metric> queue = new LinkedBlockingQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread workerThread;

    @Value("${abm.database.batch-size:1000}")
    private int batchSize;

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
        if(!queue.offer(metric))
            log.error("Queue is full");
    }

    private void processQueue() {
        List<Metric> batch = new ArrayList<>(batchSize);
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                Metric m = queue.take();

                batch.add(m);

                queue.drainTo(batch, batchSize - 1);

                saveBatch(batch);

                batch.clear();
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
        try {
            metricsRepository.saveAll(batch);
        } catch (Exception e) {
            log.error("Failed to save global metrics batch of size {}", batch.size(), e);
        }
    }

    private void flushRemaining() {
        List<Metric> batch = new ArrayList<>();
        queue.drainTo(batch);
        saveBatch(batch);
    }
}