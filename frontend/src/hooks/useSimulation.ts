import { simulationApi } from '@/api/simulation';
import type { formSchema } from '@/routes/config/-components/config-form/schema';
import {
	useSimulationStore,
	type StoredHouseholdConfig,
} from '@/store/useSimulationStore';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type z from 'zod';

/**
 * Hook to start the simulation
 */
export function useStartSimulation() {
	const queryClient = useQueryClient();
	const { setRunning, setRunId } = useSimulationStore();

	return useMutation({
		mutationFn: simulationApi.start,
		onSuccess: (data) => {
			// Extract run ID from the ApiResponse
			const runId = data.runId ?? Date.now();

			setRunId(runId);
			setRunning(true);

			toast.success('Simulation Started', {
				description: `Run ID: ${runId}`,
			});

			// Invalidate runs query to refetch the list
			queryClient.invalidateQueries({ queryKey: ['metrics', 'runs'] });
		},
		onError: (error: Error) => {
			toast.error('Failed to start simulation', {
				description: error.message,
			});
		},
	});
}

/**
 * Hook to stop the simulation
 */
export function useStopSimulation() {
	const queryClient = useQueryClient();
	const { setRunning, setRunId } = useSimulationStore();

	return useMutation({
		mutationFn: simulationApi.stop,
		onSuccess: () => {
			setRunning(false);
			setRunId(null);

			toast.success('Simulation Stopped');

			// Invalidate runs query to refetch the list
			queryClient.invalidateQueries({ queryKey: ['metrics', 'runs'] });
		},
		onError: (error: Error) => {
			toast.error('Failed to stop simulation', {
				description: error.message,
			});
		},
	});
}

/**
 * Hook to configure the simulation
 */
export function useConfigureSimulation() {
	const { setConfigured, setHouseholds } = useSimulationStore();

	return useMutation({
		mutationFn: async (config: z.infer<typeof formSchema>) => {
			// Store household data before sending to API
			const households: StoredHouseholdConfig[] =
				config.householdConfigs.map((hc) => ({
					householdName: hc.householdName,
					appliances: hc.applianceConfigs.map((ac) => ({
						applianceName: ac.applianceName,
						householdName: hc.householdName,
					})),
				}));

			// Store in state first
			setHouseholds(households);

			// Then send to API
			return simulationApi.configure(config);
		},
		onSuccess: () => {
			setConfigured(true);
			toast.success('Configuration Uploaded', {
				description: 'You can now start the simulation.',
			});
		},
		onError: (error: Error) => {
			setConfigured(false);
			setHouseholds([]);
			toast.error('Configuration Failed', {
				description: error.message,
			});
		},
	});
}
