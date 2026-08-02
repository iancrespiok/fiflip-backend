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
| `KAFKA_BOOTSTRAP_SERVERS` | Endpoint del broker (Redpanda Cloud u otro) | `xxxxx.any.us-east-1.mpx.prd.cloud.redpanda.com:9092` |
| `KAFKA_SECURITY_PROTOCOL` | Protocolo de seguridad | `SASL_SSL` |
| `KAFKA_SASL_MECHANISM` | Mecanismo SASL | `SCRAM-SHA-256` |
| `KAFKA_SASL_JAAS_CONFIG` | Config JAAS con user/pass del cluster | `org.apache.kafka.common.security.scram.ScramLoginModule required username="..." password="...";` |
| `RESEND_API_KEY` | API key de Resend (se usa vía HTTP API, no SMTP — la mayoría de los hosts cloud bloquean el puerto SMTP saliente) | `re_xxxxx` |
| `MAIL_FROM` | Remitente de los emails | `onboarding@resend.dev` (hasta verificar dominio propio) |
| `LEAD_NOTIFY_EMAIL` | A dónde llegan las notificaciones de leads | `crespi.ian@gmail.com` |
| `FRONTEND_ORIGIN` | Origen permitido por CORS | `https://fiflip-landing.vercel.app` |

## Cómo conseguir las credenciales

**Redpanda Cloud Serverless** (https://redpanda.com) — no pide tarjeta para el trial:
1. Creá una cuenta → se crea un cluster `welcome` automáticamente.
2. Andá a **Topics** → creá `leads.renovation` y `leads.investor`.
3. Andá a **Security** → creá un usuario (SASL) con contraseña generada → mecanismo **SCRAM-SHA-256**.
4. Armá `KAFKA_SASL_JAAS_CONFIG` así:
   ```
   org.apache.kafka.common.security.scram.ScramLoginModule required username="TU_USUARIO" password="TU_PASSWORD";
   ```
5. El `KAFKA_BOOTSTRAP_SERVERS` está en el Overview del cluster, pestaña "Kafka API" de "How to connect".
6. `KAFKA_SECURITY_PROTOCOL=SASL_SSL` y `KAFKA_SASL_MECHANISM=SCRAM-SHA-256`.

(Confluent Cloud es una alternativa válida — misma idea, pero pide tarjeta desde el arranque aunque no cobre durante el trial. Usa `PlainLoginModule` y mecanismo `PLAIN` en vez de SCRAM.)

**Resend** (https://resend.com):
1. Creá una cuenta, andá a "API Keys" → "Create API Key".
2. Esa key es `RESEND_API_KEY`.
3. Mientras no verifiques un dominio propio, `MAIL_FROM` tiene que ser `onboarding@resend.dev`, y solo se puede mandar a la casilla con la que te registraste en Resend.
4. **Importante**: mandamos el email por la API HTTP de Resend (`api.resend.com`), no por SMTP — Railway y la mayoría de los hosts cloud bloquean el puerto SMTP saliente (465/587) para evitar spam, así que SMTP se cuelga ahí.

## Deploy en Railway

1. En [railway.app](https://railway.app), "New Project" → "Deploy from GitHub repo" → elegí `fiflip-backend`.
2. Railway detecta el `Dockerfile` y lo builda solo.
3. En la pestaña "Variables", cargá todas las de la tabla de arriba.
4. Una vez deployado, copiá la URL pública (Settings → Networking → "Generate Domain").
5. Esa URL es el `VITE_API_URL` que hay que setear en Vercel (proyecto `fiflip-landing` → Settings → Environment Variables).

## Correr local

```bash
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export RESEND_API_KEY=tu-api-key-de-resend
mvn spring-boot:run
```
