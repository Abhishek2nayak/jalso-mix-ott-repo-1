import { z } from 'zod';

export const HealthResponseSchema = z.object({
  status: z.enum(['ok', 'error']),
  version: z.string(),
  uptime: z.number(),
});

export type HealthResponse = z.infer<typeof HealthResponseSchema>;

export const APP_NAME = 'Apna Play';
export const DEFAULT_LANGUAGE = 'hi';
export const SUPPORTED_LANGUAGES = ['hi', 'en', 'ta', 'te', 'ml', 'kn', 'bn', 'mr'] as const;

export const ApiErrorSchema = z.object({
  error: z.object({
    code: z.string(),
    message: z.string(),
    requestId: z.string(),
  }),
});

export type ApiError = z.infer<typeof ApiErrorSchema>;
