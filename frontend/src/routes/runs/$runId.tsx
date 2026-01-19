import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/runs/$runId')({
	loader: ({ params }) => {
		return { title: `Run ${params.runId}` };
	},
	component: RouteComponent,
});

function RouteComponent() {
	return <div>Hello "/runs/$runId"!</div>;
}
