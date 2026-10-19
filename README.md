# Fueler API — primera iteración

Spring Boot 3.5.6 / Java 17 / MongoDB 7 / Maven.

## Arranque local

1. `docker compose up -d`
2. Define `FUELER_ADMIN_KEY` con un secreto largo y aleatorio (no lo subas a Git).
   - Git Bash: `export FUELER_ADMIN_KEY="$(openssl rand -hex 32)"` (guarda el valor en un gestor de secretos).
3. `mvn spring-boot:run`
4. Salud: `curl http://localhost:8080/actuator/health`

## Prueba del flujo

En Git Bash, define `ADMIN_KEY` con el mismo valor que `FUELER_ADMIN_KEY`.

Crear empresa:
```bash
curl -s -X POST http://localhost:8080/api/v1/admin/clients \
 -H "X-Admin-Key: $ADMIN_KEY" -H 'Content-Type: application/json' \
 -d '{"name":"Transportes Demo"}'
```
Copia el `id` de la empresa como `CLIENT_ID`.

Solicitar activación (simula Flutter):
```bash
curl -s -X POST http://localhost:8080/api/v1/activations \
 -H 'Content-Type: application/json' \
 -d '{"installationId":"demo-device-001","label":"Pixel de pruebas"}'
```
Copia `requestId` como `DEVICE_ID`.

Aprobar dispositivo (el token aparece UNA sola vez):
```bash
curl -s -X POST "http://localhost:8080/api/v1/admin/devices/$DEVICE_ID/approve" \
 -H "X-Admin-Key: $ADMIN_KEY" -H 'Content-Type: application/json' \
 -d "{\"clientId\":\"$CLIENT_ID\"}"
```
Copia `token` como `DEVICE_TOKEN`. Entrégalo al dispositivo por un canal seguro.

Crear vehículo:
```bash
curl -s -X POST http://localhost:8080/api/v1/vehicles \
 -H "Authorization: Bearer $DEVICE_TOKEN" -H 'Content-Type: application/json' \
 -d '{"plate":"ABC123","description":"Camioneta 1"}'
```
Consultar vehículos:
```bash
curl -s http://localhost:8080/api/v1/vehicles -H "Authorization: Bearer $DEVICE_TOKEN"
```

## Alcance y seguridad

- `clientId` de operaciones se obtiene del token validado, nunca del JSON recibido.
- Se guarda SHA-256 del token aleatorio de 256 bits, no el token original.
- La aprobación entrega el token una sola vez. Revocar invalida inmediatamente su uso.
- `X-Admin-Key` es un mecanismo provisional para desarrollo; reemplazar por autenticación administrativa robusta antes de producción.
- La solicitud pública de activación aún necesita rate limiting, antifraude y controles de abuso antes de exponerla en internet.
- Solo desarrollo local: Mongo sin autenticación y HTTP local. En producción usar TLS, credenciales Mongo, redes privadas y gestión de secretos.
- Índices Mongo: para asegurar su creación en desarrollo, añadir `spring.data.mongodb.auto-index-creation: true`; planificar índices con migraciones controladas para producción.
- Falta endpoint de entrega/recuperación segura de token en Flutter; en esta iteración el administrador lo transfiere manualmente.
- Próximas iteraciones: pruebas de aislamiento, operadores, precios, registros de combustible, auditoría, paginación, migración desde Sheets.
