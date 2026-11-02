import { z } from 'zod';

export const HealthResponseSchema = z.object({
  status: z.enum(['ok', 'error']),
  version: z.string(),
  uptime: z.number(),
});

export type HealthResponseType = z.infer<typeof HealthResponseSchema>;
