import { ScrollArea } from '@/components/ui/scroll-area';
import { ConfigForm } from '@/routes/config/-components/config-form';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/config/')({
	staticData: {
		title: 'Configuration',
	},
	component: RouteComponent,
});

function RouteComponent() {
	return (
		<div className='flex flex-col h-full overflow-hidden'>
			<ScrollArea className='h-full'>
				<div className='p-1'>
					<ConfigForm />
				</div>
			</ScrollArea>
		</div>
	);
}
