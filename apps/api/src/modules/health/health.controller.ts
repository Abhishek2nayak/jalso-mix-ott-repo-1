import type { Request, Response } from 'express';
import { getHealth, getReadiness } from './health.service.js';

export function healthCheck(_req: Request, res: Response): void {
  res.json(getHealth());
}

export async function readinessCheck(_req: Request, res: Response): Promise<void> {
  const result = await getReadiness();
  res.status(result.healthy ? 200 : 503).json(result.data);
}
