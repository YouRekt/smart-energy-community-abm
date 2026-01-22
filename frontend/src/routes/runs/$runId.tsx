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
import { createFileRoute, Link } from '@tanstack/react-router';
import { ArrowLeft } from 'lucide-react';

export const Route = createFileRoute('/runs/$runId')({
	loader: ({ params }) => {
		return { title: `Run ${params.runId}` };
	},
	component: RunDetailPage,
});

const WINDOW_SIZES = [50, 100, 500, 1000, 0] as const; // 0 = all data

function RunDetailPage() {
	const { runId } = Route.useParams();
	const numericRunId = parseInt(runId, 10);
	const [windowSize, setWindowSize] = useState<number>(100);

	// Pass runId to hooks to fetch historical data (no polling)
	const { data: consumptionData, isLoading: isConsLoading } =
		useCommunityConsumption(windowSize || 10000, numericRunId);
	const { data: productionData, isLoading: isProdLoading } =
		useCommunityProduction(windowSize || 10000, numericRunId);
	const { data: batteryData, isLoading: isBattLoading } = useBatteryCharge(
		windowSize || 10000,
		numericRunId,
	);

	return (
		<div className='space-y-6'>
			{/* Header */}
			<div className='flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between'>
				<div className='flex items-center gap-4'>
					<Link to='/runs'>
						<Button variant='ghost' size='icon'>
							<ArrowLeft className='h-5 w-5' />
						</Button>
					</Link>
					<div>
						<h1 className='text-3xl font-bold tracking-tight'>
							Simulation Run #{runId}
						</h1>
						<p className='text-muted-foreground'>
							Historical data from this simulation run
						</p>
					</div>
				</div>

				{/* Window Size Selector */}
				<div className='flex gap-2 flex-wrap'>
					{WINDOW_SIZES.map((size) => (
						<Button
							key={size}
							variant={
								windowSize === size ? 'default' : 'outline'
							}
							size='sm'
							onClick={() => setWindowSize(size)}>
							{size === 0 ? 'All' : `Last ${size}`}
						</Button>
					))}
				</div>
			</div>

			{/* Charts Grid */}
			<div className='grid grid-cols-1 gap-6 lg:grid-cols-2'>
				<ConsumptionChart
					title='Community Consumption'
					description='Green energy vs grid energy consumption'
					data={consumptionData}
					isLoading={isConsLoading}
				/>
				<ProductionChart
					title='Community Production'
					description='Total energy production from renewable sources'
					data={productionData}
					isLoading={isProdLoading}
				/>
			</div>

			{/* Battery Chart - Full Width */}
			<BatteryChart
				title='Battery Charge Level'
				description='Community battery storage state'
				data={batteryData}
				isLoading={isBattLoading}
			/>
		</div>
	);
}
