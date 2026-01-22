import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { useSimulationRuns } from '@/hooks/useMetrics';
import { Link, createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/runs/')({
	staticData: {
		title: 'Previous Runs',
	},
	component: RunsListPage,
});

function RunsListPage() {
	const { data: runs, isLoading, isError, error } = useSimulationRuns();

	return (
		<div className='space-y-6'>
			<div>
				<h1 className='text-3xl font-bold tracking-tight'>
					Previous Runs
				</h1>
				<p className='text-muted-foreground'>
					View and analyze past simulation runs
				</p>
			</div>

			{isLoading ? (
				<div className='grid gap-4 md:grid-cols-2 lg:grid-cols-3'>
					{Array.from({ length: 6 }).map((_, i) => (
						<Skeleton key={i} className='h-32' />
					))}
				</div>
			) : isError ? (
				<Card>
					<CardContent className='py-8'>
						<p className='text-center text-destructive'>
							Error loading runs: {error?.message}
						</p>
					</CardContent>
				</Card>
			) : !runs || runs.length === 0 ? (
				<Card>
					<CardContent className='py-8'>
						<p className='text-center text-muted-foreground'>
							No simulation runs found. Start a simulation to see
							results here.
						</p>
					</CardContent>
				</Card>
			) : (
				<div className='grid gap-4 md:grid-cols-2 lg:grid-cols-3'>
					{runs.map((run) => (
						<Link
							key={run.id}
							to='/runs/$runId'
							params={{ runId: String(run.id) }}>
							<Card className='hover:bg-muted/50 transition-colors cursor-pointer h-full'>
								<CardHeader>
									<div className='flex items-center justify-between'>
										<CardTitle className='text-lg'>
											Run #{run.id}
										</CardTitle>
										<StatusBadge status={run.status} />
									</div>
								</CardHeader>
								<CardContent className='space-y-2'>
									<div className='text-sm'>
										<span className='text-muted-foreground'>
											Started:{' '}
										</span>
										<span>
											{formatDateTime(run.startTime)}
										</span>
									</div>
									{run.endTime && (
										<div className='text-sm'>
											<span className='text-muted-foreground'>
												Ended:{' '}
											</span>
											<span>
												{formatDateTime(run.endTime)}
											</span>
										</div>
									)}
									{run.endTime && run.startTime && (
										<div className='text-sm'>
											<span className='text-muted-foreground'>
												Duration:{' '}
											</span>
											<span>
												{formatDuration(
													run.startTime,
													run.endTime,
												)}
											</span>
										</div>
									)}
								</CardContent>
							</Card>
						</Link>
					))}
				</div>
			)}
		</div>
	);
}

function StatusBadge({
	status,
}: {
	status: 'RUNNING' | 'COMPLETED' | 'FAILED';
}) {
	const colors = {
		RUNNING: 'bg-green-500/20 text-green-500 border-green-500/30',
		COMPLETED: 'bg-blue-500/20 text-blue-500 border-blue-500/30',
		FAILED: 'bg-red-500/20 text-red-500 border-red-500/30',
	};

	return (
		<span
			className={`px-2 py-1 rounded-full text-xs font-medium border ${colors[status]}`}>
			{status}
		</span>
	);
}

function formatDateTime(dateString: string): string {
	try {
		return new Date(dateString).toLocaleString();
	} catch {
		return dateString;
	}
}

function formatDuration(start: string, end: string): string {
	try {
		const startDate = new Date(start);
		const endDate = new Date(end);
		const diffMs = endDate.getTime() - startDate.getTime();

		const seconds = Math.floor(diffMs / 1000);
		const minutes = Math.floor(seconds / 60);
		const hours = Math.floor(minutes / 60);

		if (hours > 0) {
			return `${hours}h ${minutes % 60}m`;
		} else if (minutes > 0) {
			return `${minutes}m ${seconds % 60}s`;
		} else {
			return `${seconds}s`;
		}
	} catch {
		return '-';
	}
}
