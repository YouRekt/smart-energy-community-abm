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
		<div className='@2xl:overflow-hidden'>
			<ConfigForm />
		</div>
	);
}
