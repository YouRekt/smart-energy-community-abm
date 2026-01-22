import { isPercentageContext } from '@/hooks/use-percentage';
import { useState } from 'react';

interface IsPercentageProviderProps {
	children: React.ReactNode;
}

function IsPercentageProvider({ children }: IsPercentageProviderProps) {
	const [isPercentage, setPercentage] = useState(false);

	const value = {
		isPercentage,
		setPercentage,
	};

	return (
		<isPercentageContext.Provider value={value}>
			{children}
		</isPercentageContext.Provider>
	);
}

export { IsPercentageProvider };
