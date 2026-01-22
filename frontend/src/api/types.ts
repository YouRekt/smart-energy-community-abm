export interface MetricPoint {
	tick: number;
	value: number;
}

export interface StackedMetric {
	timestamp: number;
	greenEnergy: number;
	gridEnergy: number;
}

// API returns 'green' and 'grid' arrays with {tick, value} objects
export interface StackedMetricResponse {
	green: MetricPoint[];
	grid: MetricPoint[];
}

export interface SimulationRun {
	id: number;
	startTime: string;
	endTime?: string;
	status: 'RUNNING' | 'COMPLETED' | 'FAILED';
}

export interface ApiResponse {
	message: string;
	runId?: number | null;
}
