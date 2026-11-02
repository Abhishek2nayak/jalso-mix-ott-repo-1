# Apna Play

Apna Play is a production OTT streaming platform (Hotstar/Netflix-style) supporting movies, series, micro dramas, music, podcasts, and news. It is built as a pnpm + Turborepo monorepo with TypeScript everywhere (`strict: true`).

## Apps & Packages

| Path                  | What                                                       | Stack                                                                                       |
| --------------------- | ---------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| `apps/api`            | Backend REST API                                           | Express + TypeScript, Prisma (PostgreSQL), Zod validation, pino logging, Vitest + Supertest |
| `apps/mobile`         | Android + iOS app (later Android TV)                       | Expo (React Native) + Expo Router                                                           |
| `apps/web`            | Public website / web app                                   | Next.js (App Router)                                                                        |
| `apps/admin`          | Content admin panel                                        | Vite + React + React-Admin                                                                  |
| `packages/shared`     | Zod schemas, shared types, constants                       | TypeScript                                                                                  |
| `packages/api-client` | Typed fetch client for the API                             | TypeScript                                                                                  |
| `packages/config`     | Shared tsconfig bases, ESLint flat config, Prettier config | —                                                                                           |

All packages use the scope `@apnaplay/*`.

## Backend Module Pattern

Each module in `apps/api/src/modules/<name>/` has:

- `<name>.routes.ts` — URL wiring + middleware only
- `<name>.controller.ts` — reads request, sends response only
- `<name>.service.ts` — business logic
- `<name>.repository.ts` — Prisma queries (only file that touches Prisma)
- `<name>.schemas.ts` — Zod schemas
- `<name>.test.ts` — tests

A module never imports another module's repository; it calls that module's service.

## API Error Shape

```json
{
  "error": {
    "code": "STRING_CODE",
    "message": "Human-readable message",
    "requestId": "uuid"
  }
}
```

## Commands

```bash
# Start local services (Postgres + Redis)
docker compose up -d

# Install dependencies
pnpm install

# Generate Prisma client
cd apps/api && npx prisma generate

# Run all apps in dev mode
pnpm dev

# Run a single app
pnpm dev --filter @apnaplay/api
pnpm dev --filter @apnaplay/web
pnpm dev --filter @apnaplay/admin
pnpm dev --filter @apnaplay/mobile

# Lint, typecheck, test, build (all via Turborepo)
pnpm lint
pnpm typecheck
pnpm test
pnpm build

# Format
pnpm format

# Create a Prisma migration
cd apps/api && npx prisma migrate dev --name <migration_name>
```

## Rules

- Every new endpoint ships with tests. No PR without passing `lint`, `typecheck`, `test`.
- Validate every request with Zod. Share schemas through `@apnaplay/shared`.
- Money is stored as integer paise (₹199 = 19900).
- Phone numbers in E.164 (`+919876543210`).
- Times stored in UTC, shown in IST.
- Secrets only in `.env`, never in code or commits.
- Plan, payment, device-limit and premium-access decisions happen ONLY on the backend; never trust the client.
- Video files never pass through the API; the API only returns short-lived signed URLs (Bunny Stream).
- The API stays stateless (no in-memory sessions) so it can scale horizontally.
- Keep PRs small: one feature or fix per PR.

## Planned Modules (not built yet)

- auth (OTP via MSG91)
- devices (device management + limits)
- catalog (movies, series, episodes, music, podcasts, news)
- playback (signed URL generation via Bunny Stream)
- progress (watch history, continue watching)
- payments (Razorpay subscriptions)
- ads (ad insertion)
- unlocks (premium content gating)
- watch-party (synchronized viewing)
