import { useState } from 'react';

import { ConsumptionChart } from '@/components/charts';
import { Button } from '@/components/ui/button';
import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from '@/components/ui/card';
import { useApplianceConsumption } from '@/hooks/useMetrics';
import { useSimulationStore } from '@/store/useSimulationStore';
import { Link, createFileRoute } from '@tanstack/react-router';
import { ArrowLeft, Plug } from 'lucide-react';

export const Route = createFileRoute(
	'/households/$householdName/$applianceName',
)({
	staticData: {
		title: 'Appliance Details',
	},
	component: ApplianceDetailPage,
});

const WINDOW_SIZES = [50, 100, 500, 1000] as const;

function ApplianceDetailPage() {
	const { householdName, applianceName } = Route.useParams();
	const [windowSize, setWindowSize] = useState<number>(100);
	const { isRunning, currentRunId } = useSimulationStore();

	const { data: applianceData, isLoading } = useApplianceConsumption(
		householdName,
		applianceName,
		windowSize,
	);

	return (
		<div className='space-y-6'>
			{/* Header */}
			<div className='flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between'>
				<div className='flex items-center gap-4'>
					<Link to='/households'>
						<Button variant='ghost' size='icon'>
							<ArrowLeft className='h-4 w-4' />
						</Button>
					</Link>
					<div>
						<h1 className='text-3xl font-bold tracking-tight'>
							{applianceName}
						</h1>
						<p className='text-muted-foreground'>
							{householdName}
							{isRunning && ` • Run #${currentRunId}`}
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
							Last {size}
						</Button>
					))}
				</div>
			</div>

			{/* Breadcrumb */}
			<div className='flex items-center gap-2 text-sm text-muted-foreground'>
				<Link to='/' className='hover:text-foreground'>
					Dashboard
				</Link>
				<span>/</span>
				<Link to='/households' className='hover:text-foreground'>
					Households
				</Link>
				<span>/</span>
				<span>{householdName}</span>
				<span>/</span>
				<span className='text-foreground'>{applianceName}</span>
			</div>

			{/* Consumption Chart */}
			<ConsumptionChart
				title={`${applianceName} Consumption`}
				description={`Energy consumption for ${applianceName} in ${householdName}`}
				data={applianceData}
				isLoading={isLoading}
			/>

			{/* Appliance Info Card */}
			<Card>
				<CardHeader>
					<CardTitle className='flex items-center gap-2'>
						<Plug className='h-5 w-5' />
						Appliance Information
					</CardTitle>
					<CardDescription>
						Details about this appliance
					</CardDescription>
				</CardHeader>
				<CardContent>
					<dl className='grid grid-cols-2 gap-4'>
						<div>
							<dt className='text-sm text-muted-foreground'>
								Appliance Name
							</dt>
							<dd className='font-medium'>{applianceName}</dd>
						</div>
						<div>
							<dt className='text-sm text-muted-foreground'>
								Household
							</dt>
							<dd className='font-medium'>{householdName}</dd>
						</div>
					</dl>
				</CardContent>
			</Card>
		</div>
	);
}
