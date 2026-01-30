import { useState } from 'react';

import {
	BatteryChart,
	ConsumptionChart,
	ProductionChart,
} from '@/components/charts';
import { Button } from '@/components/ui/button';
import {
	useBatteryCharge,
	useCommunityConsumption,
	useCommunityProduction,
} from '@/hooks/useMetrics';
import { getTickMultiplier } from '@/lib/utils';
import { useSimulationStore } from '@/store/useSimulationStore';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/')({
	staticData: {
		title: 'Dashboard',
	},
	component: Dashboard,
});

const WINDOW_SIZES = [50, 100, 500, 1000] as const;

function Dashboard() {
	const [windowSize, setWindowSize] = useState<number>(100);
	const { isRunning, isConfigured, currentRunId, tickConfig } =
		useSimulationStore();

	const { data: consumptionData, isLoading: isConsLoading } =
		useCommunityConsumption(windowSize);
	const { data: productionData, isLoading: isProdLoading } =
		useCommunityProduction(windowSize);
	const { data: batteryData, isLoading: isBattLoading } =
		useBatteryCharge(windowSize);

	return (
		<div className='space-y-6 overflow-auto'>
			<div className='flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between'>
				<div>
					<h1 className='text-3xl font-bold tracking-tight'>
						Live Dashboard
					</h1>
					<p className='text-muted-foreground'>
						{isRunning ? (
							<span className='flex items-center gap-2'>
								<span className='relative flex h-3 w-3'>
									<span className='animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75'></span>
									<span className='relative inline-flex rounded-full h-3 w-3 bg-green-500'></span>
								</span>
								Simulation running (Run #{currentRunId})
							</span>
						) : isConfigured ? (
							'Simulation stopped. Press Start to begin.'
						) : (
							'No configuration uploaded. Go to Configuration to set up.'
						)}
					</p>
				</div>

				<div className='flex gap-2 flex-wrap'>
					{WINDOW_SIZES.map((size) => (
						<Button
							key={size}
							variant={
								windowSize === size ? 'default' : 'outline'
							}
							size='sm'
							onClick={() => setWindowSize(size)}>
							Last {size}
						</Button>
					))}
				</div>
			</div>

			<div className='grid grid-cols-1 gap-6 lg:grid-cols-2'>
				<ConsumptionChart
					title='Community Consumption'
					description='Green energy vs grid energy consumption'
					data={consumptionData}
					isLoading={isConsLoading}
					tickMultiplier={getTickMultiplier(tickConfig)}
				/>
				<ProductionChart
					title='Community Production'
					description='Total energy production from renewable sources'
					data={productionData}
					isLoading={isProdLoading}
					tickMultiplier={getTickMultiplier(tickConfig)}
				/>
			</div>

			<BatteryChart
				title='Battery Charge Level'
				description='Community battery storage state'
				data={batteryData}
				isLoading={isBattLoading}
				tickMultiplier={getTickMultiplier(tickConfig)}
			/>
		</div>
	);
}
