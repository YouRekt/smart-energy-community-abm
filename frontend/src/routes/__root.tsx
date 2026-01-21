import { AppSidebar } from '@/components/app-sidebar';
import { Breadcrumbs } from '@/components/breadcrumbs';
import { ThemeProvider } from '@/components/theme-provider';
import { ToasterProvider } from '@/components/toaster-provider';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import {
	SidebarInset,
	SidebarProvider,
	SidebarTrigger,
} from '@/components/ui/sidebar';
import { createRootRoute, Outlet } from '@tanstack/react-router';

const RootLayout = () => {
	return (
		<ThemeProvider defaultTheme='dark' storageKey='vite-ui-theme'>
			<ToasterProvider>
				<SidebarProvider className='max-h-dvh'>
					<AppSidebar />
					<SidebarInset>
						<header className='flex h-16 shrink-0 items-center gap-2 border-b px-4'>
							<SidebarTrigger className='-ml-1' />
							<Separator
								orientation='vertical'
								className='mr-2 data-[orientation=vertical]:h-4'
							/>
							<Breadcrumbs />
						</header>
						<div className='flex flex-1 flex-col gap-4 p-4 max-h-[calc(100vh-4rem)]'>
							<ScrollArea className='max-h-[calc(100vh-4rem)] overflow-y-auto'>
								<Outlet />
							</ScrollArea>
						</div>
					</SidebarInset>
				</SidebarProvider>
			</ToasterProvider>
		</ThemeProvider>
	);
};

export const Route = createRootRoute({ component: RootLayout });
