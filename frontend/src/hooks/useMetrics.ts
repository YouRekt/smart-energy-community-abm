import { analysisApi, metricsApi } from '@/api/simulation';
import type {
	MetricPoint,
	StackedMetric,
	StackedMetricResponse,
} from '@/api/types';
import { useSimulationStore } from '@/store/useSimulationStore';
import { useQuery } from '@tanstack/react-query';

/**
 * Transform stacked response into chart-friendly format
 */
const transformStackedData = (data: StackedMetricResponse): StackedMetric[] => {
	// Check if data exists and has arrays with content
	if (
		!data ||
		!Array.isArray(data.green) ||
		!Array.isArray(data.grid) ||
		data.green.length === 0
	) {
		return [];
	}

	return data.green.map((point: MetricPoint, index: number) => ({
		timestamp: point.tick,
		greenEnergy: point.value,
		gridEnergy: data.grid[index]?.value || 0,
	}));
};

/**
 * Hook for fetching community consumption metrics with optional polling
 */
export function useCommunityConsumption(limit?: number, runId?: number) {
	const isRunning = useSimulationStore((s) => s.isRunning);
	const isLive = !runId;

	return useQuery({
		queryKey: [
			'metrics',
			'consumption',
			'community',
			runId || 'live',
			limit,
		],
		queryFn: async () => {
			const data = await metricsApi.getCommunityConsumption({
				limit,
				runId,
			});
			return transformStackedData(data);
		},
		refetchInterval: isLive && isRunning ? 1000 : false,
		refetchOnWindowFocus: false,
	});
}

/**
 * Hook for fetching household consumption metrics with optional polling
 */
export function useHouseholdConsumption(
	householdName: string,
	limit?: number,
	runId?: number,
) {
	const isRunning = useSimulationStore((s) => s.isRunning);
	const isLive = !runId;

	return useQuery({
		queryKey: [
			'metrics',
			'consumption',
			'household',
			householdName,
			runId || 'live',
			limit,
		],
		queryFn: async () => {
			const data = await metricsApi.getHouseholdConsumption(
				householdName,
				{
					limit,
					runId,
				},
			);
			return transformStackedData(data);
		},
		refetchInterval: isLive && isRunning ? 1000 : false,
		refetchOnWindowFocus: false,
		enabled: !!householdName,
	});
}

/**
 * Hook for fetching appliance consumption metrics with optional polling
 */
export function useApplianceConsumption(
	householdName: string,
	applianceName: string,
	limit?: number,
	runId?: number,
) {
	const isRunning = useSimulationStore((s) => s.isRunning);
	const isLive = !runId;

	return useQuery({
		queryKey: [
			'metrics',
			'consumption',
			'appliance',
			householdName,
			applianceName,
			runId || 'live',
			limit,
		],
		queryFn: async () => {
			const data = await metricsApi.getApplianceConsumption(
				householdName,
				applianceName,
				{
					limit,
					runId,
				},
			);
			return transformStackedData(data);
		},
		refetchInterval: isLive && isRunning ? 1000 : false,
		refetchOnWindowFocus: false,
		enabled: !!householdName && !!applianceName,
	});
}

/**
 * Hook for fetching community production metrics with optional polling
 */
export function useCommunityProduction(limit?: number, runId?: number) {
	const isRunning = useSimulationStore((s) => s.isRunning);
	const isLive = !runId;

	return useQuery({
		queryKey: [
			'metrics',
			'production',
			'community',
			runId || 'live',
			limit,
		],
		queryFn: () => metricsApi.getCommunityProduction({ limit, runId }),
		refetchInterval: isLive && isRunning ? 1000 : false,
		refetchOnWindowFocus: false,
	});
}

/**
 * Hook for fetching battery charge metrics with optional polling
 */
export function useBatteryCharge(limit?: number, runId?: number) {
	const isRunning = useSimulationStore((s) => s.isRunning);
	const isLive = !runId;

	return useQuery({
		queryKey: ['metrics', 'battery', 'charge', runId || 'live', limit],
		queryFn: () => metricsApi.getBatteryCharge({ limit, runId }),
		refetchInterval: isLive && isRunning ? 1000 : false,
		refetchOnWindowFocus: false,
	});
}

/**
 * Hook for fetching simulation runs
 */
export function useSimulationRuns() {
	return useQuery({
		queryKey: ['metrics', 'runs'],
		queryFn: metricsApi.getRuns,
	});
}

/**
 * Hook for fetching run analysis
 */
export function useRunAnalysis(runId: number) {
	return useQuery({
		queryKey: ['analysis', runId],
		queryFn: () => analysisApi.getRunAnalysis(runId),
		enabled: !!runId && !isNaN(runId),
		retry: false,
	});
}
