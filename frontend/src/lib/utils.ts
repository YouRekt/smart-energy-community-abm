import type { TickConfig } from '@/store/useSimulationStore';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
	return twMerge(clsx(inputs));
}

export function getTickMultiplier(tickConfig: TickConfig | null) {
	if (!tickConfig) {
		return 1;
	}

	let multiplier = tickConfig.tickAmount;

	switch (tickConfig.tickUnit) {
		default:
		case 'second':
			multiplier *= 1;
			break;
		case 'minute':
			multiplier *= 60;
			break;
		case 'hour':
			multiplier *= 3600;
			break;
		case 'day':
			multiplier *= 86400;
			break;
	}

	return multiplier;
}
