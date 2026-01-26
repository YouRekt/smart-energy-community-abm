import type { SimulationAnalysisResponse } from '@/api/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Activity, Battery, CheckCircle2, Zap } from 'lucide-react';

interface RunAnalysisVisualizerProps {
	data?: SimulationAnalysisResponse;
	isLoading: boolean;
	tickMultiplier?: number;
}

export function RunAnalysisVisualizer({
	data,
	isLoading,
	tickMultiplier,
}: RunAnalysisVisualizerProps) {
	if (isLoading) {
		return (
			<div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
				{[...Array(4)].map((_, i) => (
					<div
						key={i}
						className='h-32 rounded-xl bg-muted/50 animate-pulse'
					/>
				))}
			</div>
		);
	}

	if (!data || !tickMultiplier) return null;

	return (
		<div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
			<Card>
				<CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
					<CardTitle className='text-sm font-medium'>
						Energy Independence
					</CardTitle>
					<Zap className='h-4 w-4 text-muted-foreground' />
				</CardHeader>
				<CardContent>
					<div className='text-2xl font-bold'>
						{data.selfSufficiencyRatio.toFixed(2)}
						{' %'}
					</div>
					<p className='text-xs text-muted-foreground'>
						Self Sufficiency
					</p>
					<div className='mt-4 text-sm text-muted-foreground'>
						<div className='flex justify-between'>
							<span>Self Consumption:</span>
							<span className='font-semibold'>
								{data.selfConsumptionRatio.toFixed(2)}
								{' %'}
							</span>
						</div>
					</div>
				</CardContent>
			</Card>

			<Card>
				<CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
					<CardTitle className='text-sm font-medium'>
						Grid Impact
					</CardTitle>
					<Activity className='h-4 w-4 text-muted-foreground' />
				</CardHeader>
				<CardContent>
					<div className='text-2xl font-bold'>
						{(data.maxGridPeak / tickMultiplier).toFixed(2)}
						{' kW'}
					</div>
					<p className='text-xs text-muted-foreground'>
						Max Grid Peak
					</p>
					<div className='mt-4 text-sm text-muted-foreground'>
						<div className='flex justify-between'>
							<span>Volatility Variation Coefficient:</span>
							<span className='font-semibold'>
								{data.gridVolatilityCV.toFixed(2)}
								{' %'}
							</span>
						</div>
					</div>
				</CardContent>
			</Card>

			<Card>
				<CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
					<CardTitle className='text-sm font-medium'>
						Battery Stats
					</CardTitle>
					<Battery className='h-4 w-4 text-muted-foreground' />
				</CardHeader>
				<CardContent>
					<div className='text-2xl font-bold'>
						{data.equivalentFullCycles.toFixed(2)}
					</div>
					<p className='text-xs text-muted-foreground'>Full Cycles</p>
					<div className='mt-4 text-sm text-muted-foreground space-y-1'>
						<div className='flex justify-between'>
							<span>Efficiency:</span>
							<span className='font-semibold'>
								{data.batteryEfficiency.toFixed(2)}
								{' %'}
							</span>
						</div>
						<div className='flex justify-between'>
							<span>Loss Ratio:</span>
							<span className='font-semibold'>
								{data.energyLossRatio.toFixed(2)}
								{' %'}
							</span>
						</div>
						<div className='flex justify-between'>
							<span>Battery Full Ratio:</span>
							<span className='font-semibold'>
								{data.fullRatio.toFixed(2)}
								{' %'}
							</span>
						</div>
						<div className='flex justify-between'>
							<span>Battery Empty Ratio:</span>
							<span className='font-semibold'>
								{data.emptyRatio.toFixed(2)}
								{' %'}
							</span>
						</div>
					</div>
				</CardContent>
			</Card>

			<Card>
				<CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
					<CardTitle className='text-sm font-medium'>
						Reliability & Fairness
					</CardTitle>
					<CheckCircle2 className='h-4 w-4 text-muted-foreground' />
				</CardHeader>
				<CardContent>
					<div className='text-2xl font-bold'>
						{data.taskCompletionRate.toFixed(2)}
						{' %'}
					</div>
					<p className='text-xs text-muted-foreground'>
						Task Completion
					</p>
					<div className='mt-4 text-sm text-muted-foreground space-y-1'>
						<div className='flex justify-between'>
							<span>Fairness:</span>
							<span className='font-semibold'>
								{data.fairnessIndex.toFixed(4)}
							</span>
						</div>
						<div className='flex justify-between'>
							<span>Acceptance:</span>
							<span className='font-semibold'>
								{data.taskAcceptanceRate.toFixed(2)}
								{' %'}
							</span>
						</div>
					</div>
				</CardContent>
			</Card>
		</div>
	);
}
