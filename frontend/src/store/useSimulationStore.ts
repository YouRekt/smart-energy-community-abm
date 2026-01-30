import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// Simplified config types for storage
interface StoredApplianceConfig {
	applianceName: string;
	householdName: string;
}

interface StoredHouseholdConfig {
	householdName: string;
	appliances: StoredApplianceConfig[];
}

interface TickConfig {
	tickUnit: 'second' | 'minute' | 'hour' | 'day';
	tickAmount: number;
}

interface SimulationState {
	isConfigured: boolean;
	isRunning: boolean;
	currentRunId: number | null;
	households: StoredHouseholdConfig[];
	tickConfig: TickConfig | null;
	setConfigured: (status: boolean) => void;
	setRunning: (status: boolean) => void;
	setRunId: (id: number | null) => void;
	setHouseholds: (households: StoredHouseholdConfig[]) => void;
	setTickConfig: (config: TickConfig) => void;
	reset: () => void;
}

export const useSimulationStore = create<SimulationState>()(
	persist(
		(set) => ({
			isConfigured: false,
			isRunning: false,
			currentRunId: null,
			households: [],
			tickConfig: null,
			setConfigured: (status) => set({ isConfigured: status }),
			setRunning: (status) => set({ isRunning: status }),
			setRunId: (id) => set({ currentRunId: id }),
			setHouseholds: (households) => set({ households }),
			setTickConfig: (tickConfig) => set({ tickConfig }),
			reset: () =>
				set({
					isConfigured: false,
					isRunning: false,
					currentRunId: null,
					households: [],
					tickConfig: null,
				}),
		}),
		{ name: 'simulation-storage' },
	),
);

export type { StoredApplianceConfig, StoredHouseholdConfig, TickConfig };
