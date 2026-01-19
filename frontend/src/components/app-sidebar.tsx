import * as React from 'react';

import { ModeToggle } from '@/components/mode-toggle';
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
import { Link } from '@tanstack/react-router';

// This is sample data.
const data = {
	navMain: [
		{
			title: 'Simulation',
			url: '/',
			items: [
				{
					title: 'Dashboard',
					url: '/',
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

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
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
							<SidebarMenu>
								{item.items.map((item) => (
									<SidebarMenuItem key={item.url}>
										<Link to={item.url}>
											{({ isActive }) => (
												<SidebarMenuButton
													isActive={isActive}
												>
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
