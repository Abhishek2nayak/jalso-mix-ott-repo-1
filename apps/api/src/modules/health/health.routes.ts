import { Router, type IRouter } from 'express';
import { healthCheck, readinessCheck } from './health.controller.js';

export const healthRouter: IRouter = Router();

healthRouter.get('/', healthCheck);
healthRouter.get('/ready', readinessCheck);
