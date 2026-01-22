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

interface SimulationState {
	isConfigured: boolean;
	isRunning: boolean;
	currentRunId: number | null;
	households: StoredHouseholdConfig[];
	setConfigured: (status: boolean) => void;
	setRunning: (status: boolean) => void;
	setRunId: (id: number | null) => void;
	setHouseholds: (households: StoredHouseholdConfig[]) => void;
	reset: () => void;
}

export const useSimulationStore = create<SimulationState>()(
	persist(
		(set) => ({
			isConfigured: false,
			isRunning: false,
			currentRunId: null,
			households: [],
			setConfigured: (status) => set({ isConfigured: status }),
			setRunning: (status) => set({ isRunning: status }),
			setRunId: (id) => set({ currentRunId: id }),
			setHouseholds: (households) => set({ households }),
			reset: () =>
				set({
					isConfigured: false,
					isRunning: false,
					currentRunId: null,
					households: [],
				}),
		}),
		{ name: 'simulation-storage' },
	),
);

export type { StoredApplianceConfig, StoredHouseholdConfig };
