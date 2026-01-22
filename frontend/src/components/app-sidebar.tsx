import { Play, Square } from 'lucide-react';
import * as React from 'react';

import { ModeToggle } from '@/components/mode-toggle';
import { Button } from '@/components/ui/button';
import {
	Sidebar,
	SidebarContent,
	SidebarGroup,
	SidebarGroupContent,
	SidebarGroupLabel,
	SidebarHeader,
	SidebarMenu,
	SidebarMenuButton,
	SidebarMenuItem,
	SidebarRail,
} from '@/components/ui/sidebar';
import { useStartSimulation, useStopSimulation } from '@/hooks/useSimulation';
import { useSimulationStore } from '@/store/useSimulationStore';
import { Link } from '@tanstack/react-router';

const data = {
	navMain: [
		{
			title: 'Simulation',
			items: [
				{
					title: 'Dashboard',
					url: '/',
				},
				{
					title: 'Households',
					url: '/households',
				},
				{
					title: 'Configuration',
					url: '/config',
				},
				{
					title: 'Previous Runs',
					url: '/runs',
				},
			],
		},
	],
};

function SimulationControls() {
	const { isConfigured, isRunning } = useSimulationStore();
	const startMutation = useStartSimulation();
	const stopMutation = useStopSimulation();

	const handleStart = () => {
		startMutation.mutate();
	};

	const handleStop = () => {
		stopMutation.mutate();
	};

	return (
		<div className='flex gap-2 px-2 pb-2'>
			{!isRunning ? (
				<Button
					onClick={handleStart}
					disabled={!isConfigured || startMutation.isPending}
					className='flex-1'
					size='sm'>
					<Play className='mr-2 h-4 w-4' />
					{startMutation.isPending ? 'Starting...' : 'Start'}
				</Button>
			) : (
				<Button
					onClick={handleStop}
					disabled={stopMutation.isPending}
					variant='destructive'
					className='flex-1'
					size='sm'>
					<Square className='mr-2 h-4 w-4' />
					{stopMutation.isPending ? 'Stopping...' : 'Stop'}
				</Button>
			)}
		</div>
	);
}

function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
	return (
		<Sidebar {...props}>
			<SidebarHeader>
				<div className='flex items-center gap-4 px-2'>
					<img src='/favicon.png' alt='Logo' className='size-20' />
					<h1 className='text-xl font-bold'>
						<span className='text-primary'>
							Green Energy Community
						</span>
					</h1>
				</div>
				<div className='flex items-center justify-between gap-2'>
					<p className='pl-2'>Toggle theme</p>
					<ModeToggle />
				</div>
			</SidebarHeader>
			<SidebarContent>
				{/* We create a SidebarGroup for each parent. */}
				{data.navMain.map((item) => (
					<SidebarGroup key={item.title}>
						<SidebarGroupLabel>{item.title}</SidebarGroupLabel>
						<SidebarGroupContent>
							{/* Simulation Controls */}
							<SimulationControls />
							<SidebarMenu>
								{item.items.map((item) => (
									<SidebarMenuItem key={item.url}>
										<Link to={item.url}>
											{({ isActive }) => (
												<SidebarMenuButton
													isActive={isActive}>
													{item.title}
												</SidebarMenuButton>
											)}
										</Link>
									</SidebarMenuItem>
								))}
							</SidebarMenu>
						</SidebarGroupContent>
					</SidebarGroup>
				))}
			</SidebarContent>
			<SidebarRail />
		</Sidebar>
	);
}

export { AppSidebar };
