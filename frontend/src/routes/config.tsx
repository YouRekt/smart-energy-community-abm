import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/config')({
	staticData: {
		title: 'Configuration',
	},
	component: RouteComponent,
});

function RouteComponent() {
	return <div>Hello "/config"!</div>;
}
