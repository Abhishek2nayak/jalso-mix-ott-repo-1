# Apna Play — Architecture

## Overview

Apna Play is a production OTT streaming platform (Hotstar/Netflix-style) supporting movies, series, micro dramas, music, podcasts, and news.

## Monorepo Layout

| Path                  | What                              | Stack                                                                    |
| --------------------- | --------------------------------- | ------------------------------------------------------------------------ |
| `apps/api`            | Backend REST API                  | Express + TypeScript, Prisma (PostgreSQL), Zod, pino, Vitest + Supertest |
| `apps/mobile`         | Android + iOS app                 | Expo (React Native) + Expo Router                                        |
| `apps/web`            | Public website                    | Next.js (App Router)                                                     |
| `apps/admin`          | Content admin panel               | Vite + React + React-Admin                                               |
| `packages/shared`     | Zod schemas, types, constants     | TypeScript                                                               |
| `packages/api-client` | Typed fetch client for the API    | TypeScript                                                               |
| `packages/config`     | Shared tsconfig, ESLint, Prettier | —                                                                        |

## Backend Module Pattern

Each module in `apps/api/src/modules/<name>/` follows:

```
<name>.routes.ts      → URL wiring + middleware
<name>.controller.ts  → reads request, sends response
<name>.service.ts     → business logic
<name>.repository.ts  → Prisma queries
<name>.schemas.ts     → Zod schemas
<name>.test.ts        → tests
```

- A module never imports another module's repository; it calls that module's service.
- Routes only wire URLs and middleware.
- Controllers only read the request and send the response.
- Business logic lives in services.
- Only repositories touch Prisma.

## API Error Shape

All API errors use one JSON shape:

```json
{
  "error": {
    "code": "STRING_CODE",
    "message": "Human-readable message",
    "requestId": "uuid"
  }
}
```

## Data Conventions

- Money: integer paise (₹199 = 19900)
- Phone numbers: E.164 (`+919876543210`)
- Times: stored in UTC, shown in IST
- Video delivery: short-lived signed URLs (Bunny Stream), never through the API
