# ⛽ Fueler API

Backend multi-tenant para registrar, consultar y administrar cargas de combustible, vehículos, operadores, gasolineras y dispositivos autorizados.

**Stack:** Java 17 · Spring Boot 4.1.1 · Spring Security · Spring Data MongoDB · Maven · Docker · OpenAPI / Swagger UI.

> **Estado:** proyecto en desarrollo. La API y su despliegue están en construcción; las funcionalidades deben validarse mediante pruebas de integración antes de considerarse listas para producción.

## Funcionalidades

### Implementadas en el backend

- **Clientes y dispositivos:** registro público de instalaciones, aprobación administrativa y acceso mediante token.
- **Autenticación por roles:** `ROLE_ADMIN` para administración y `ROLE_DEVICE` para operaciones de la aplicación.
- **Aislamiento multi-tenant:** cada consulta y operación de negocio se limita al `clientId` del dispositivo autenticado.
- **Catálogos:** vehículos, operadores y gasolineras asociados al cliente.
- **Gasolineras:** registro explícito, consulta y desactivación; no se crean automáticamente al registrar una carga.
- **Cargas de combustible:** alta, búsqueda paginada, detalle y corrección con control de versión e historial.
- **Idempotencia:** combinación única de `clientId` y `requestId` para evitar cargas duplicadas.
- **Cálculos:** importe total y rendimiento en km/L a partir de los datos registrados.
- **Último precio sugerido:** consulta del último precio registrado para una gasolinera y tipo de combustible, dentro del mismo cliente.
- **Normalización de referencias:** utilidad compartida `ReferenceNormalizer` para referencias de catálogos.
- **Documentación OpenAPI:** Swagger UI con grupos de administración, dispositivos y registro público.

### Pendientes / mejoras previstas

- Exportación a **Excel** de cargas, respetando filtros y tenant.
- Historial diario de precios de combustible.
- Verificación integral de filtros, paginación, validaciones y documentación OpenAPI.
- Integración completa y pruebas desde la aplicación Flutter.
- Revisar la opción de listar exclusivamente gasolineras activas para el selector de Flutter.

## Arquitectura

Se sigue una **arquitectura hexagonal** (puertos y adaptadores):

```text
HTTP / Flutter
     │
     ▼
Controllers + Request/Response + Web Mappers
     │
     ▼
Application Input Ports
     │
     ▼
Use Cases + Commands
     │
     ▼
Application Output Ports
     │
     ▼
Persistence Adapters + Mongo Mappers + Repositories
     │
     ▼
MongoDB
```

Paquete base: `net.iozamudioa.fueler`.

```text
fueler-api/
├── pom.xml
└── src/main/java/net/iozamudioa/fueler/
    ├── domain/
    │   └── util/ReferenceNormalizer.java
    ├── application/
    │   ├── command/
    │   ├── ports/in/
    │   ├── ports/out/
    │   └── usecase/
    └── infrastructure/
        ├── config/
        └── adapters/
            ├── in/
            │   ├── security/
            │   └── web/
            │       ├── admin/
            │       ├── mapper/
            │       └── model/
            └── out/persistence/
                └── repository/mongo/
```

## Seguridad y multi-tenancy

Fueler no usa un login convencional para la aplicación móvil:

1. Una instalación Flutter se identifica con un UUID.
2. Solicita registro mediante `POST /api/v1/devices`.
3. Un administrador autoriza el dispositivo.
4. El dispositivo utiliza su token en `Authorization: Bearer <TOKEN>`.
5. El backend recupera `deviceId` y `clientId` desde el dispositivo autenticado (`DevicePrincipal`).

Los tokens se almacenan mediante hash, no en texto plano. Un dispositivo debe estar en estado `ACTIVE` para autenticarse.

| Tipo de acceso | Autenticación | Rutas |
|---|---|---|
| Público | Sin credenciales | `POST /api/v1/devices` |
| Administración | Cabecera `X-Admin-Key` | `/api/v1/admin/**` |
| Dispositivo | `Authorization: Bearer <TOKEN>` | `/api/v1/**` excepto rutas anteriores |

**Regla esencial:** Flutter **nunca** determina el `clientId` de una operación; se obtiene del contexto autenticado en el servidor.

## API y Swagger

Dominio previsto: **https://api.fueler.ioza.dev**.

| Recurso | Ruta |
|---|---|
| Registro de dispositivo | `POST /api/v1/devices` |
| Administración de clientes | `/api/v1/admin/clients/**` |
| Administración de dispositivos | `/api/v1/admin/devices/**` |
| Cargas de combustible | `/api/v1/fuel` |
| Último precio sugerido | `GET /api/v1/fuel/prices/latest` |
| Gasolineras | `/api/v1/gas-stations` |
| Vehículos | `/api/v1/vehicles` |
| Operadores | `/api/v1/operators` |
| Swagger UI | `/swagger-ui/index.html` |
| OpenAPI JSON | `/v3/api-docs` |
| Salud | `/actuator/health` |

> Las rutas de catálogos corresponden al diseño actual; revisar sus mappings exactos en los controllers al publicar la documentación.

### Ejemplo: último precio

```http
GET /api/v1/fuel/prices/latest?gasStationReference=PEMEX%20CENTRO&fuelType=DIESEL
Authorization: Bearer <TOKEN>
```

Respuesta de ejemplo (`200 OK`):

```json
{
  "gasStationReference": "PEMEX CENTRO",
  "fuelType": "DIESEL",
  "pricePerLiter": 25.80,
  "date": "2026-10-19"
}
```

Si no existe un precio registrado, responde `204 No Content`.

La sugerencia se obtiene de `fuel_records`, filtrando por cliente, gasolinera y combustible, y ordenando por `createdAt` descendente. El usuario puede editar el precio antes de guardar la siguiente carga.

### Swagger UI

La documentación está organizada en tres grupos:

- `01-admin`: operaciones con `X-Admin-Key`.
- `02-device`: operaciones con Bearer token.
- `03-public`: registro inicial sin autenticación.

**Precaución:** en producción, restringir el acceso público a Swagger UI y `/v3/api-docs/**` desde el proxy o una capa de acceso adicional si no se desea exponer la documentación.

## Persistencia MongoDB

Colección principal: `fuel_records`.

Índices relevantes:

```javascript
{ clientId: 1, requestId: 1 } // unique
{ clientId: 1, date: -1 }
{ clientId: 1, gasStationReference: 1, fuelType: 1, createdAt: -1, _id: -1 }
```

Cada carga conserva su `gasStationReference`, incluso si posteriormente se desactiva la gasolinera del catálogo. Las nuevas selecciones deben apuntar a una gasolinera activa del mismo cliente.

## Configuración sensible

**No versionar `application.yml`, contraseñas, claves administrativas ni cadenas de conexión.** La configuración de producción vive fuera del repositorio y se monta en el contenedor en modo solo lectura.

`.gitignore` recomendado:

```gitignore
**/application.yml
**/application-prod.yml
**/target/
.env
.env.*
```

`.dockerignore` recomendado:

```dockerignore
.git
**/target
**/application.yml
**/application-*.yml
.env
.env.*
```

> Si alguna credencial ya estuvo en Git, eliminar el archivo del commit actual no borra el historial. Rotar los secretos comprometidos.

El archivo de producción se ubicará, por ejemplo, en:

```text
/home/iozamudioa/docker/fueler-config/application.yml
```

Propiedades esperadas (valores de ejemplo, **no** secretos reales):

```yaml
spring:
  application:
    name: fueler
  data:
    mongodb:
      uri: ${MONGODB_URI}

server:
  port: 8080
  forward-headers-strategy: framework

fueler:
  admin-key: ${FUELER_ADMIN_KEY}
```

En producción se pueden establecer directamente los valores dentro del YAML protegido, o inyectarlos como variables de entorno. El archivo debe ser legible por el proceso correspondiente y no quedar expuesto públicamente.

## Docker y despliegue

El despliegue se realiza en el servidor: **`git pull` → `docker compose build` → `docker compose up`**.

El Dockerfile existente utiliza dos etapas: Maven para compilar y Eclipse Temurin Java 17 para ejecutar el JAR.

`docker-compose.yml` de referencia, basado en la configuración del servidor:

```yaml
services:
  fueler-api:
    build:
      context: .
      dockerfile: dockerfile
    container_name: fueler-api
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_ADDITIONAL_LOCATION: file:/config/application.yml
      TZ: America/Mexico_City
      FAVORITES_VALIDATION_ZONE: America/Mexico_City
    volumes:
      - /home/iozamudioa/docker/fueler-api/media:/media
      - /home/iozamudioa/docker/fueler-config/application.yml:/config/application.yml:ro
    networks:
      - infra-net

networks:
  infra-net:
    external: true
```

Crear el archivo externo **antes** de levantar el servicio y asegurarse de que no se copie al JAR durante el build.

Comandos de despliegue:

```bash
git pull
docker compose up -d --build
docker logs -f fueler-api
```

Verificación del montaje:

```bash
docker exec fueler-api test -f /config/application.yml && echo "Configuración montada"
```

### Proxy inverso

Configurar Nginx Proxy Manager, conectado a la red Docker `infra-net`:

| Parámetro | Valor |
|---|---|
| Dominio | `api.fueler.ioza.dev` |
| Protocolo al contenedor | `http` |
| Host destino | `fueler-api` |
| Puerto destino | `8080` |
| TLS | HTTPS con certificado válido |

Configurar en Cloudflare el DNS o la ruta de túnel correspondiente. No es necesario publicar el puerto `8080` del contenedor hacia Internet.

## Próximos pasos

1. Probar el despliegue, la conectividad a MongoDB y el acceso HTTPS.
2. Verificar los tres grupos Swagger y la autenticación desde `Authorize`.
3. Probar aislamiento entre clientes, alta de cargas, búsqueda y correcciones.
4. Implementar exportación a Excel con los filtros existentes.
5. Conectar y validar los flujos completos desde Flutter.

---

**Fueler** · Backend de gestión de combustible · En desarrollo.
