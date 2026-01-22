export const client = async <T>(
	endpoint: string,
	options?: RequestInit,
): Promise<T> => {
	const method = options?.method?.toUpperCase() || 'GET';
	const headers: HeadersInit = { ...options?.headers };

	// Only set Content-Type for requests with a body (not GET/HEAD)
	if (method !== 'GET' && method !== 'HEAD') {
		(headers as Record<string, string>)['Content-Type'] =
			'application/json';
	}

	const response = await fetch(`/api${endpoint}`, {
		...options,
		headers,
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
