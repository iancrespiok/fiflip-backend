# fiflip-backend

Recibe los leads de la landing (`fiflip-landing`) por HTTP, los publica en tópicos
de Kafka (`leads.renovation`, `leads.investor`) y un consumer interno manda un
email de notificación por cada lead recibido.

```
Formulario (React) → POST /api/leads/* → Kafka → consumer → email
```

## Endpoints

- `POST /api/leads/renovation` — `{ nombre, contacto, tipo, ciudad, medidas, descripcion }`
- `POST /api/leads/investor` — `{ nombre, contacto, monto, mensaje }`
- `GET /actuator/health` — health check

## Variables de entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `PORT` | Puerto del servidor (Railway lo setea solo) | `8080` |
| `KAFKA_BOOTSTRAP_SERVERS` | Endpoint del broker de Confluent Cloud | `pkc-xxxxx.us-east-1.aws.confluent.cloud:9092` |
| `KAFKA_SECURITY_PROTOCOL` | Protocolo de seguridad | `SASL_SSL` |
| `KAFKA_SASL_MECHANISM` | Mecanismo SASL | `PLAIN` |
| `KAFKA_SASL_JAAS_CONFIG` | Config JAAS con la API key/secret de Confluent | `org.apache.kafka.common.security.plain.PlainLoginModule required username="API_KEY" password="API_SECRET";` |
| `MAIL_HOST` | Host SMTP (Resend) | `smtp.resend.com` |
| `MAIL_PORT` | Puerto SMTP | `465` |
| `MAIL_USERNAME` | Usuario SMTP de Resend | `resend` |
| `MAIL_PASSWORD` | API key de Resend (se usa como password SMTP) | `re_xxxxx` |
| `MAIL_FROM` | Remitente de los emails | `onboarding@resend.dev` (hasta verificar dominio propio) |
| `LEAD_NOTIFY_EMAIL` | A dónde llegan las notificaciones de leads | `crespi.ian@gmail.com` |
| `FRONTEND_ORIGIN` | Origen permitido por CORS | `https://fiflip-landing.vercel.app` |

## Cómo conseguir las credenciales

**Confluent Cloud** (https://confluent.cloud):
1. Creá una cuenta (te dan ~USD 400 de crédito gratis por 30/60 días).
2. "Add environment" → nombre `fiflip` → "Create cluster" → tipo **Basic**, elegí la región/proveedor más cercano.
3. Dentro del cluster, andá a **Topics** → "Create topic" → creá `leads.renovation` y `leads.investor` (particiones: 1 está bien para este volumen).
4. Andá a **API Keys** → "Create key" → alcance "Cluster" → esto te da un `Key` y un `Secret`. **Copiá el secret ya, no se puede volver a ver.**
5. Armá `KAFKA_SASL_JAAS_CONFIG` así:
   ```
   org.apache.kafka.common.security.plain.PlainLoginModule required username="TU_API_KEY" password="TU_API_SECRET";
   ```
6. El `KAFKA_BOOTSTRAP_SERVERS` está en **Cluster Settings → Endpoints** (Bootstrap server).
7. `KAFKA_SECURITY_PROTOCOL=SASL_SSL` y `KAFKA_SASL_MECHANISM=PLAIN`.

**Resend** (https://resend.com):
1. Creá una cuenta, andá a "API Keys" → "Create API Key".
2. Esa key es `MAIL_PASSWORD`. `MAIL_USERNAME` es literalmente `resend`.
3. Mientras no verifiques un dominio propio, `MAIL_FROM` tiene que ser `onboarding@resend.dev`.

## Deploy en Railway

1. En [railway.app](https://railway.app), "New Project" → "Deploy from GitHub repo" → elegí `fiflip-backend`.
2. Railway detecta el `Dockerfile` y lo builda solo.
3. En la pestaña "Variables", cargá todas las de la tabla de arriba.
4. Una vez deployado, copiá la URL pública (Settings → Networking → "Generate Domain").
5. Esa URL es el `VITE_API_URL` que hay que setear en Vercel (proyecto `fiflip-landing` → Settings → Environment Variables).

## Correr local

```bash
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export MAIL_PASSWORD=tu-api-key-de-resend
mvn spring-boot:run
```
