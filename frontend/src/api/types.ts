export interface MetricPoint {
	timestamp: number;
	time: string;
	value: number;
	name: string;
}

export interface StackedMetric {
	timestamp: number;
	greenEnergy: number;
	gridEnergy: number;
}

export interface StackedMetricResponse {
	greenEnergy: MetricPoint[];
	gridEnergy: MetricPoint[];
}

export interface SimulationRun {
	id: number;
	startTime: string;
	endTime?: string;
	status: 'RUNNING' | 'COMPLETED' | 'FAILED';
}
