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

const chartConfig = {
	greenEnergy: {
		label: 'Green Energy (kWh)',
		color: 'hsl(142, 76%, 36%)',
	},
	gridEnergy: {
		label: 'Grid Energy (kWh)',
		color: 'hsl(0, 84%, 60%)',
	},
} satisfies ChartConfig;

interface ConsumptionChartProps {
	title: string;
	description?: string;
	data?: StackedMetric[];
	isLoading?: boolean;
}

export function ConsumptionChart({
	title,
	description,
	data,
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

	if (!data || data.length === 0) {
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
					className='h-[250px] w-full'>
					<AreaChart accessibilityLayer data={data}>
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
							tickMargin={10}
							tickFormatter={(value) => `${value}`}
						/>
						<YAxis
							tickLine={false}
							axisLine={false}
							tickMargin={10}
							tickFormatter={(value) => `${value.toFixed(1)}`}
						/>
						<ChartTooltip
							content={
								<ChartTooltipContent
									labelKey='timestamp'
									indicator='dot'
								/>
							}
						/>
						<Area
							dataKey='gridEnergy'
							type='monotone'
							fill='url(#fillGrid)'
							stroke='var(--color-gridEnergy)'
							stackId='a'
						/>
						<Area
							dataKey='greenEnergy'
							type='monotone'
							fill='url(#fillGreen)'
							stroke='var(--color-greenEnergy)'
							stackId='a'
						/>
						<ChartLegend content={<ChartLegendContent />} />
					</AreaChart>
				</ChartContainer>
			</CardContent>
		</Card>
	);
}
