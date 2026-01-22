import { createContext, useContext } from 'react';

type IsPercentageState = {
	isPercentage: boolean;
	setPercentage: (isPercentage: boolean) => void;
};

const initialState: IsPercentageState = {
	isPercentage: false,
	setPercentage: () => null,
};

const isPercentageContext = createContext<IsPercentageState>(initialState);

const useIsPercentage = () => {
	const context = useContext(isPercentageContext);
	if (context === undefined)
		throw new Error(
			'useIsPercentage must be used within a IsPercentageProvider',
		);

	return context;
};

export { isPercentageContext, useIsPercentage };
