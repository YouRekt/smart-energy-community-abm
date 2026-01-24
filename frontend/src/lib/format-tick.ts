import type { TickConfig } from '@/store/useSimulationStore';

/**
 * Formats a tick count into a human-readable duration string based on the tick configuration.
 * @param tick - The tick number
 * @param config - The tick configuration (unit and amount)
 * @returns Formatted time string, e.g. "1h 30m" or "Day 2 05:00" depending on duration
 */
export function formatTick(tick: number, config: TickConfig | null): string {
	if (!config) return tick.toString();

	const { tickUnit, tickAmount } = config;
	const totalSecondsPerTick =
		tickAmount *
		(tickUnit === 'second'
			? 1
			: tickUnit === 'minute'
				? 60
				: tickUnit === 'hour'
					? 3600
					: 86400); // day

	const totalSeconds = tick * totalSecondsPerTick;

	// Simple formatting logic based on total duration
	if (totalSeconds < 60) {
		return `${totalSeconds}s`;
	} else if (totalSeconds < 3600) {
		const mins = Math.floor(totalSeconds / 60);
		const secs = totalSeconds % 60;
		return secs > 0 ? `${mins}m ${secs}s` : `${mins}m`;
	} else if (totalSeconds < 86400) {
		const hours = Math.floor(totalSeconds / 3600);
		const mins = Math.floor((totalSeconds % 3600) / 60);
		return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`;
	} else {
		const days = Math.floor(totalSeconds / 86400);
		const hours = Math.floor((totalSeconds % 86400) / 3600);
		return hours > 0 ? `${days}d ${hours}h` : `${days}d`;
	}
}

/**
 * Formats a tick count into a Date string relative to today 00:00:00.
 * @param tick - The tick number
 * @param config - The tick configuration (unit and amount)
 * @returns Formatted date string
 */
export function formatTickToDate(
	tick: number,
	config: TickConfig | null,
): string {
	const now = new Date();
	now.setHours(0, 0, 0, 0);

	if (!config) {
		// Fallback if no config: just add seconds
		return new Date(now.getTime() + tick * 1000).toLocaleString();
	}

	const { tickUnit, tickAmount } = config;
	const secondsPerTick =
		tickAmount *
		(tickUnit === 'second'
			? 1
			: tickUnit === 'minute'
				? 60
				: tickUnit === 'hour'
					? 3600
					: 86400);

	const date = new Date(now.getTime() + tick * secondsPerTick * 1000);

	// Format: "Jan 24, 14:30" or similar
	return date.toLocaleString('en-US', {
		month: 'short',
		day: 'numeric',
		hour: 'numeric',
		minute: '2-digit',
		hour12: false,
	});
}
