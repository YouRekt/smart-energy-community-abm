import { useTheme } from '@/components/theme-provider';
import { Toaster } from 'sonner';

interface ToasterProviderProps {
	children: React.ReactNode;
}

function ToasterProvider({ children }: ToasterProviderProps) {
	const { theme } = useTheme();

	return (
		<>
			{children}
			<Toaster richColors theme={theme} />
		</>
	);
}

export { ToasterProvider };
