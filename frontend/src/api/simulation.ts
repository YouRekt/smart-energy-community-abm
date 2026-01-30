import type { formSchema } from '@/routes/config/-components/config-form/schema';
import type z from 'zod';
import { client } from './client';
import type {
	ApiResponse,
	MetricPoint,
	SimulationAnalysisResponse,
	SimulationRun,
	StackedMetricResponse,
} from './types';

// --- Query Parameters ---
interface MetricsParams {
	lastTick?: number;
	runId?: number;
	limit?: number;
}

const buildSearchParams = (params: MetricsParams): string => {
	const searchParams = new URLSearchParams();
	if (params.lastTick !== undefined)
		searchParams.set('lastTick', params.lastTick.toString());
	if (params.runId !== undefined)
		searchParams.set('runId', params.runId.toString());
	if (params.limit !== undefined)
		searchParams.set('limit', params.limit.toString());
	const queryString = searchParams.toString();
	return queryString ? `?${queryString}` : '';
};

// --- Simulation API ---
export const simulationApi = {
	start: () => client<ApiResponse>('/simulation/start', { method: 'POST' }),

	stop: () => client<ApiResponse>('/simulation/stop', { method: 'POST' }),

	configure: (config: z.infer<typeof formSchema>) =>
		client<ApiResponse>('/simulation/config', {
			method: 'POST',
			body: JSON.stringify(config),
		}),
};

// --- Metrics API ---
export const metricsApi = {
	getRuns: () => client<SimulationRun[]>('/metrics/runs'),

	getCommunityConsumption: (params: MetricsParams = {}) =>
		client<StackedMetricResponse>(
			`/metrics/consumption/community${buildSearchParams(params)}`,
		),

	getHouseholdConsumption: (
		householdName: string,
		params: MetricsParams = {},
	) =>
		client<StackedMetricResponse>(
			`/metrics/consumption/household/${encodeURIComponent(householdName)}${buildSearchParams(params)}`,
		),

	getApplianceConsumption: (
		householdName: string,
		applianceName: string,
		params: MetricsParams = {},
	) =>
		client<StackedMetricResponse>(
			`/metrics/consumption/appliance/${encodeURIComponent(householdName)}/${encodeURIComponent(applianceName)}${buildSearchParams(params)}`,
		),

	getCommunityProduction: (params: MetricsParams = {}) =>
		client<MetricPoint[]>(
			`/metrics/production/community${buildSearchParams(params)}`,
		),

	getSourceProduction: (sourceName: string, params: MetricsParams = {}) =>
		client<MetricPoint[]>(
			`/metrics/production/source/${encodeURIComponent(sourceName)}${buildSearchParams(params)}`,
		),

	getBatteryCharge: (params: MetricsParams = {}) =>
		client<MetricPoint[]>(
			`/metrics/battery/charge${buildSearchParams(params)}`,
		),
};

// --- Analysis API ---
export const analysisApi = {
	getRunAnalysis: (runId: number) =>
		client<SimulationAnalysisResponse>(`/runs/${runId}/analysis`),
};
