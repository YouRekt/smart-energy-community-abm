/**
 * Formats a tick count into a human-readable duration string based on the tick configuration.
 * @param tick - The tick number
 * @param tickMultiplier - The tick multiplier
 * @returns Formatted time string, e.g. "1h 30m" or "Day 2 05:00" depending on duration
 */
export function formatTick(tick: number, tickMultiplier: number): string {
	const totalSeconds = tick * tickMultiplier;

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
