import { checkDatabaseConnection } from './health.repository.js';
import type { HealthResponseType } from './health.schemas.js';

const startTime = Date.now();

export function getHealth(): HealthResponseType {
  return {
    status: 'ok',
    version: process.env.npm_package_version || '0.0.0',
    uptime: (Date.now() - startTime) / 1000,
  };
}

export async function getReadiness(): Promise<{ healthy: boolean; data: HealthResponseType }> {
  const dbHealthy = await checkDatabaseConnection();
  const health = getHealth();

  return {
    healthy: dbHealthy,
    data: {
      ...health,
      status: dbHealthy ? 'ok' : 'error',
    },
  };
}
