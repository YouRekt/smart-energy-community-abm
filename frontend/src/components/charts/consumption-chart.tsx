import { Area, AreaChart, CartesianGrid, XAxis, YAxis } from 'recharts';

import type { StackedMetric } from '@/api/types';
import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from '@/components/ui/card';
import type { ChartConfig } from '@/components/ui/chart';
import {
	ChartContainer,
	ChartLegend,
	ChartLegendContent,
	ChartTooltip,
	ChartTooltipContent,
} from '@/components/ui/chart';
import { Skeleton } from '@/components/ui/skeleton';
import { formatTick } from '@/lib/format-tick';

const chartConfig = {
	greenEnergy: {
		label: 'Green Energy',
		color: 'var(--green-energy-chart)',
	},
	gridEnergy: {
		label: 'Grid Energy',
		color: 'var(--grid-energy-chart)',
	},
} satisfies ChartConfig;

interface ConsumptionChartProps {
	title: string;
	description?: string;
	data?: StackedMetric[];
	tickMultiplier?: number;
	isLoading?: boolean;
}

export function ConsumptionChart({
	title,
	description,
	data,
	tickMultiplier,
	isLoading,
}: ConsumptionChartProps) {
	if (isLoading) {
		return (
			<Card>
				<CardHeader>
					<CardTitle>{title}</CardTitle>
					{description && (
						<CardDescription>{description}</CardDescription>
					)}
				</CardHeader>
				<CardContent>
					<Skeleton className='h-[250px] w-full' />
				</CardContent>
			</Card>
		);
	}

	if (!data || data.length === 0 || !tickMultiplier) {
		return (
			<Card>
				<CardHeader>
					<CardTitle>{title}</CardTitle>
					{description && (
						<CardDescription>{description}</CardDescription>
					)}
				</CardHeader>
				<CardContent>
					<div className='h-[250px] flex items-center justify-center text-muted-foreground'>
						No data available
					</div>
				</CardContent>
			</Card>
		);
	}

	return (
		<Card>
			<CardHeader>
				<CardTitle>{title}</CardTitle>
				{description && (
					<CardDescription>{description}</CardDescription>
				)}
			</CardHeader>
			<CardContent>
				<ChartContainer
					config={chartConfig}
					className='aspect-auto h-[250px] w-full'>
					<AreaChart
						accessibilityLayer
						data={data.map((point) => ({
							...point,
							greenEnergy: point.greenEnergy / tickMultiplier,
							gridEnergy: point.gridEnergy / tickMultiplier,
						}))}>
						<defs>
							<linearGradient
								id='fillGreen'
								x1='0'
								y1='0'
								x2='0'
								y2='1'>
								<stop
									offset='5%'
									stopColor='var(--color-greenEnergy)'
									stopOpacity={0.8}
								/>
								<stop
									offset='95%'
									stopColor='var(--color-greenEnergy)'
									stopOpacity={0.1}
								/>
							</linearGradient>
							<linearGradient
								id='fillGrid'
								x1='0'
								y1='0'
								x2='0'
								y2='1'>
								<stop
									offset='5%'
									stopColor='var(--color-gridEnergy)'
									stopOpacity={0.8}
								/>
								<stop
									offset='95%'
									stopColor='var(--color-gridEnergy)'
									stopOpacity={0.1}
								/>
							</linearGradient>
						</defs>
						<CartesianGrid strokeDasharray='3 3' vertical={false} />
						<XAxis
							dataKey='timestamp'
							tickLine={false}
							axisLine={false}
							tickMargin={8}
							minTickGap={32}
							tickFormatter={(value) =>
								formatTick(value, tickMultiplier)
							}
						/>
						<YAxis
							label={{
								value: 'kW',
								angle: -90,
								position: 'insideLeft',
							}}
							tickLine={false}
							axisLine={false}
							tickMargin={8}
							tickFormatter={(value) => `${value.toFixed(1)}`}
						/>
						<ChartTooltip
							cursor={false}
							content={
								<ChartTooltipContent
									className='min-w-56'
									labelFormatter={(_, payload) =>
										formatTick(
											payload[0].payload.timestamp,
											tickMultiplier,
										)
									}
									valueFormatter={(value) =>
										`${(value as number).toFixed(3)} kW`
									}
									indicator='dot'
								/>
							}
						/>
						<Area
							dataKey='gridEnergy'
							type='step'
							fill='url(#fillGrid)'
							stroke='var(--color-gridEnergy)'
							stackId='a'
							animationDuration={100}
						/>
						<Area
							dataKey='greenEnergy'
							type='step'
							fill='url(#fillGreen)'
							stroke='var(--color-greenEnergy)'
							stackId='a'
							animationDuration={100}
						/>
						<ChartLegend content={<ChartLegendContent />} />
					</AreaChart>
				</ChartContainer>
			</CardContent>
		</Card>
	);
}
