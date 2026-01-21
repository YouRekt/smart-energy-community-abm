export const client = async <T>(
	endpoint: string,
	options?: RequestInit,
): Promise<T> => {
	const response = await fetch(`/api${endpoint}`, {
		...options,
		headers: {
			'Content-Type': 'application/json',
			...options?.headers,
		},
	});

	if (!response.ok) {
		const errorText = await response
			.text()
			.catch(() => response.statusText);
		throw new Error(errorText || `API Error: ${response.status}`);
	}

	const text = await response.text();
	return text ? JSON.parse(text) : ({} as T);
};
