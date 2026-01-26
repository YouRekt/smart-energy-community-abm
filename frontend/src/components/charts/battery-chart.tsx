import { Area, AreaChart, CartesianGrid, XAxis, YAxis } from 'recharts';

import type { MetricPoint } from '@/api/types';
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
	charge: {
		label: 'Battery Charge',
		color: 'var(--battery-chart)',
	},
} satisfies ChartConfig;

interface BatteryChartProps {
	title: string;
	description?: string;
	data?: MetricPoint[];
	isLoading?: boolean;
	tickMultiplier?: number;
}

export function BatteryChart({
	title,
	description,
	data,
	isLoading,
	tickMultiplier,
}: BatteryChartProps) {
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

	const transformedData = data.map((point) => ({
		charge: point.value / 3600,
		tick: point.tick,
	}));

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
					<AreaChart accessibilityLayer data={transformedData}>
						<defs>
							<linearGradient
								id='fillCharge'
								x1='0'
								y1='0'
								x2='0'
								y2='1'>
								<stop
									offset='5%'
									stopColor='var(--color-charge)'
									stopOpacity={0.8}
								/>
								<stop
									offset='95%'
									stopColor='var(--color-charge)'
									stopOpacity={0.1}
								/>
							</linearGradient>
						</defs>
						<CartesianGrid strokeDasharray='3 3' vertical={false} />
						<XAxis
							dataKey='tick'
							tickLine={false}
							axisLine={false}
							tickMargin={8}
							minTickGap={32}
							tickFormatter={(value) =>
								formatTick(value, tickMultiplier)
							}
						/>
						<ChartTooltip
							cursor={false}
							content={
								<ChartTooltipContent
									className='min-w-56'
									labelFormatter={(_, payload) =>
										formatTick(
											payload[0].payload.tick,
											tickMultiplier,
										)
									}
									valueFormatter={(value) =>
										`${(value as number).toFixed(3)} kWh`
									}
									indicator='dot'
								/>
							}
						/>
						<YAxis
							label={{
								value: 'kWh',
								angle: -90,
								position: 'insideLeft',
							}}
							dataKey='charge'
							tickLine={false}
							axisLine={false}
							tickMargin={8}
							tickFormatter={(value) => `${value.toFixed(1)}`}
						/>
						<Area
							dataKey='charge'
							type='step'
							fill='url(#fillCharge)'
							stroke='var(--color-charge)'
							animationDuration={100}
						/>
						<ChartLegend content={<ChartLegendContent />} />
					</AreaChart>
				</ChartContainer>
			</CardContent>
		</Card>
	);
}
