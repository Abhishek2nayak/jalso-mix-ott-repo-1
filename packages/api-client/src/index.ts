import type { HealthResponse } from '@apnaplay/shared';

export function createApiClient(baseUrl: string) {
  async function get<T>(path: string): Promise<T> {
    const res = await fetch(`${baseUrl}${path}`);
    if (!res.ok) {
      throw new Error(`API error: ${res.status} ${res.statusText}`);
    }
    return res.json() as Promise<T>;
  }

  return {
    getHealth: () => get<HealthResponse>('/health'),
  };
}

export type ApiClient = ReturnType<typeof createApiClient>;
