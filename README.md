# VetCare

Backend system for veterinary clinic management.

## Stack
- Java 25
- Spring Boot 4
- Spring Security (OAuth2 Resource Server + OIDC JWT)
- PostgreSQL 16
- Flyway
- Docker

## OIDC Authentication

The backend no longer issues JWTs. It validates `Bearer` access tokens minted by an OIDC provider (Keycloak/Auth0/Cognito) through issuer metadata and JWKS.

### Required environment

```bash
OIDC_ISSUER_URI=http://localhost:8081/realms/vetcare
OIDC_CLIENT_ID=vetcare-front
CORS_ORIGIN_ERP=http://localhost:5173
CORS_ORIGIN_CLINIC=http://localhost:5174
CORS_ORIGIN_POS=http://localhost:5175
```

### Keycloak admin sync (users/passwords)

The `/api/users` endpoints now sync identity data to Keycloak (create user, reset password, set realm role).

```bash
KEYCLOAK_ADMIN_SYNC_ENABLED=true
KEYCLOAK_BASE_URL=http://localhost:8081
KEYCLOAK_REALM=vetcare
KEYCLOAK_ADMIN_REALM=master
KEYCLOAK_ADMIN_CLIENT_ID=admin-cli
KEYCLOAK_ADMIN_CLIENT_SECRET=
KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_ADMIN_PASSWORD=admin
```

### How roles are mapped

Supported app roles: `ADMIN`, `VET`, `RECEPTION`.

The API reads roles from:
- `realm_access.roles`
- `resource_access.<client_id>.roles`
- `roles`

Mapped Spring authorities:
- `ROLE_ADMIN`
- `ROLE_VET`
- `ROLE_RECEPTION`

### `/api/users/me`

`/api/users/me` resolves the authenticated JWT principal and performs on-demand lookup/provisioning by email (case-insensitive). If no local user exists for that email, one is created automatically.

### Legacy login endpoint

`POST /api/auth/login` is deprecated and returns `410 Gone`.

## Local Keycloak (separate compose)

This repository includes a dedicated OIDC compose file, separate from your existing Postgres compose.

### Start Keycloak

From the `vetcare` root:

```bash
docker compose -f docker-compose.oidc.yml up -d
```

From another folder (absolute-path mode):

```bash
cp .env.oidc.example .env.oidc
# adjust KEYCLOAK_REALM_IMPORT_PATH / KEYCLOAK_THEMES_PATH if needed

docker compose --env-file .env.oidc -f /home/lucas/projects/vetcare/docker-compose.oidc.yml up -d
```

### Stop Keycloak

```bash
docker compose -f docker-compose.oidc.yml down
```

### Seeded realm/client/user

Imported from `infra/keycloak/realm-import/vetcare-realm.json`:
- Realm: `vetcare`
- Clients (public + PKCE): `vetcare-front`, `vetcare-clinic`, `vetcare-pos`
- Realm roles: `ADMIN`, `VET`, `RECEPTION`
- Test user: `admin@vetcare.local` / `admin123` (role `ADMIN`)

### VetCare Keycloak theme

A custom Keycloak login theme is versioned at:
- `infra/keycloak/themes/vetcare`

The realm import already sets `loginTheme=vetcare`.

If Keycloak had already been initialized before theme/realm changes, recreate OIDC data once so the realm import is reapplied:

```bash
docker compose -f docker-compose.oidc.yml down -v
docker compose -f docker-compose.oidc.yml up -d
```

### Admin console

- URL: `http://localhost:8081/admin`
- Admin user: `admin`
- Admin pass: `admin`

## Quick API test

```bash
curl -i http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <OIDC_ACCESS_TOKEN>"
```

Expect `200` with the internal user profile when the token is valid and includes one of the supported roles.
