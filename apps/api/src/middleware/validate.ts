import type { Request, Response, NextFunction } from 'express';
import type { ZodSchema } from 'zod';

interface ValidationSchemas {
  body?: ZodSchema;
  query?: ZodSchema;
  params?: ZodSchema;
}

export function validate(schemas: ValidationSchemas) {
  return (req: Request, res: Response, next: NextFunction): void => {
    const requestId = (req.headers['x-request-id'] as string) || 'unknown';

    if (schemas.body) {
      const result = schemas.body.safeParse(req.body);
      if (!result.success) {
        res.status(400).json({
          error: {
            code: 'VALIDATION_ERROR',
            message: result.error.errors.map((e) => e.message).join(', '),
            requestId,
          },
        });
        return;
      }
      req.body = result.data;
    }

    if (schemas.query) {
      const result = schemas.query.safeParse(req.query);
      if (!result.success) {
        res.status(400).json({
          error: {
            code: 'VALIDATION_ERROR',
            message: result.error.errors.map((e) => e.message).join(', '),
            requestId,
          },
        });
        return;
      }
    }

    if (schemas.params) {
      const result = schemas.params.safeParse(req.params);
      if (!result.success) {
        res.status(400).json({
          error: {
            code: 'VALIDATION_ERROR',
            message: result.error.errors.map((e) => e.message).join(', '),
            requestId,
          },
        });
        return;
      }
    }

    next();
  };
}
