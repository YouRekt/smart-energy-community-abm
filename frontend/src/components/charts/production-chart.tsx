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
import { getTickMultiplier } from '@/lib/utils';
import { useSimulationStore } from '@/store/useSimulationStore';

const chartConfig = {
	production: {
		label: 'Production',
		color: 'var(--production-chart)',
	},
} satisfies ChartConfig;

interface ProductionChartProps {
	title: string;
	description?: string;
	data?: MetricPoint[];
	isLoading?: boolean;
}

export function ProductionChart({
	title,
	description,
	data,
	isLoading,
}: ProductionChartProps) {
	const tickConfig = useSimulationStore((state) => state.tickConfig);

	const tickMultiplier = getTickMultiplier(tickConfig);

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
					className='aspect-auto h-[250px] w-full'>
					<AreaChart
						accessibilityLayer
						data={data.map((point) => ({
							...point,
							value: point.value / tickMultiplier,
						}))}>
						<defs>
							<linearGradient
								id='fillProduction'
								x1='0'
								y1='0'
								x2='0'
								y2='1'>
								<stop
									offset='5%'
									stopColor='var(--color-production)'
									stopOpacity={0.8}
								/>
								<stop
									offset='95%'
									stopColor='var(--color-production)'
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
								formatTick(value, tickConfig)
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
											payload[0].payload.tick,
											tickConfig,
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
							dataKey='value'
							name='production'
							type='step'
							fill='url(#fillProduction)'
							stroke='var(--color-production)'
							animationDuration={100}
						/>
						<ChartLegend content={<ChartLegendContent />} />
					</AreaChart>
				</ChartContainer>
			</CardContent>
		</Card>
	);
}
