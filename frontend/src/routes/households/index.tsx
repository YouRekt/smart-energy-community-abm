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
import { useHouseholdConsumption } from '@/hooks/useMetrics';
import { useSimulationStore } from '@/store/useSimulationStore';
import { Link, createFileRoute } from '@tanstack/react-router';
import { ArrowLeft, ChevronRight, Home } from 'lucide-react';

export const Route = createFileRoute('/households/')({
	staticData: {
		title: 'Households',
	},
	component: HouseholdsPage,
});

const WINDOW_SIZES = [50, 100, 500, 1000] as const;

function HouseholdsPage() {
	const [windowSize, setWindowSize] = useState<number>(100);
	const [selectedHousehold, setSelectedHousehold] = useState<string | null>(
		null,
	);
	const { households, isConfigured, isRunning, currentRunId } =
		useSimulationStore();

	const { data: householdData, isLoading } = useHouseholdConsumption(
		selectedHousehold || '',
		windowSize,
	);

	if (!isConfigured || households.length === 0) {
		return (
			<div className='space-y-6'>
				<div className='flex items-center gap-4'>
					<Link to='/'>
						<Button variant='ghost' size='icon'>
							<ArrowLeft className='h-4 w-4' />
						</Button>
					</Link>
					<h1 className='text-3xl font-bold tracking-tight'>
						Households
					</h1>
				</div>
				<Card>
					<CardContent className='py-12'>
						<div className='text-center text-muted-foreground'>
							<Home className='mx-auto h-12 w-12 mb-4 opacity-50' />
							<p className='text-lg font-medium'>
								No configuration uploaded
							</p>
							<p className='mt-2'>
								Upload a configuration to see household data.
							</p>
							<Link to='/config'>
								<Button className='mt-4'>
									Go to Configuration
								</Button>
							</Link>
						</div>
					</CardContent>
				</Card>
			</div>
		);
	}

	return (
		<div className='space-y-6'>
			{/* Header */}
			<div className='flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between'>
				<div className='flex items-center gap-4'>
					<Link to='/'>
						<Button variant='ghost' size='icon'>
							<ArrowLeft className='h-4 w-4' />
						</Button>
					</Link>
					<div>
						<h1 className='text-3xl font-bold tracking-tight'>
							Households
						</h1>
						<p className='text-muted-foreground'>
							{selectedHousehold
								? `Viewing: ${selectedHousehold}`
								: `${households.length} households configured`}
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

			<div className='grid grid-cols-1 lg:grid-cols-3 gap-6'>
				{/* Household List */}
				<Card className='lg:col-span-1'>
					<CardHeader>
						<CardTitle>Select Household</CardTitle>
						<CardDescription>
							Click a household to view its consumption
						</CardDescription>
					</CardHeader>
					<CardContent className='p-0'>
						<div className='divide-y'>
							{households.map((household) => (
								<button
									key={household.householdName}
									type='button'
									onClick={() =>
										setSelectedHousehold(
											household.householdName,
										)
									}
									className={`w-full flex items-center justify-between p-4 hover:bg-accent transition-colors text-left ${
										selectedHousehold ===
										household.householdName
											? 'bg-accent'
											: ''
									}`}>
									<div>
										<p className='font-medium'>
											{household.householdName}
										</p>
										<p className='text-sm text-muted-foreground'>
											{household.appliances.length}{' '}
											appliance
											{household.appliances.length !== 1
												? 's'
												: ''}
										</p>
									</div>
									<ChevronRight className='h-4 w-4 text-muted-foreground' />
								</button>
							))}
						</div>
					</CardContent>
				</Card>

				{/* Consumption Chart */}
				<div className='lg:col-span-2'>
					{selectedHousehold ? (
						<div className='space-y-6'>
							<ConsumptionChart
								title={`${selectedHousehold} Consumption`}
								description='Energy consumption breakdown'
								data={householdData}
								isLoading={isLoading}
							/>

							{/* Appliances for this household */}
							<Card>
								<CardHeader>
									<CardTitle>Appliances</CardTitle>
									<CardDescription>
										Drill down to individual appliances
									</CardDescription>
								</CardHeader>
								<CardContent className='p-0'>
									<div className='divide-y'>
										{households
											.find(
												(h) =>
													h.householdName ===
													selectedHousehold,
											)
											?.appliances.map((appliance) => (
												<Link
													key={
														appliance.applianceName
													}
													to='/households/$householdName/$applianceName'
													params={{
														householdName:
															selectedHousehold,
														applianceName:
															appliance.applianceName,
													}}>
													<div className='flex items-center justify-between p-4 hover:bg-accent transition-colors'>
														<p className='font-medium'>
															{
																appliance.applianceName
															}
														</p>
														<ChevronRight className='h-4 w-4 text-muted-foreground' />
													</div>
												</Link>
											))}
									</div>
								</CardContent>
							</Card>
						</div>
					) : (
						<Card>
							<CardContent className='py-12'>
								<div className='text-center text-muted-foreground'>
									<Home className='mx-auto h-12 w-12 mb-4 opacity-50' />
									<p className='text-lg font-medium'>
										Select a household
									</p>
									<p className='mt-2'>
										Click on a household from the list to
										view its consumption data.
									</p>
								</div>
							</CardContent>
						</Card>
					)}
				</div>
			</div>
		</div>
	);
}
