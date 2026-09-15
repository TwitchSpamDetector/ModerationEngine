# ModerationEngine

Motor de moderación del proyecto [TwitchSpamDetector](https://github.com/TwitchSpamDetector). Analiza mensajes de chat en tiempo real (por REST o por RabbitMQ) y devuelve un veredicto de spam/toxicidad, consultando a la API central para la decisión final según la configuración del canal.

Generado a partir del contrato `moderation-engine-openapi.yaml`.

## Stack

- Java 21 + Spring Boot 3.4
- Maven
- spring-boot-starter-web (REST)
- spring-boot-starter-amqp (RabbitMQ)
- springdoc-openapi (Swagger UI en `/api/v1/swagger-ui.html` una vez levantado)
- Lombok

## Estructura

```
controller/    -> endpoints REST (POST /analyze, GET /health)
messaging/     -> listener de RabbitMQ (vía asíncrona equivalente a /analyze)
service/       -> lógica de moderación (reglas + IA de toxicidad)
client/        -> llamada a la API central para resolver la acción final
dto/           -> request/response/error, espejo del contrato OpenAPI
enums/         -> ReasonCode y ModerationAction (valores alineados al contrato)
exception/     -> GlobalExceptionHandler -> ErrorResponse del contrato
```

## Correr en local

```bash
mvn spring-boot:run
```

Necesita un RabbitMQ corriendo (variables `RABBITMQ_HOST`, etc. en `application.yml`) y la URL de la API central en `CENTRAL_API_BASE_URL`.

## Mensajería con el Ingestor

Contrato en [`asyncapi/moderation-engine-asyncapi.yaml`](./asyncapi/moderation-engine-asyncapi.yaml).
Confirmado contra el código real del Ingestor: no hay exchange propio, el
Ingestor publica con `channel.sendToQueue(...)` directo a una cola durable
("chat-messages" por default en ambos servicios — variable `MODERATION_QUEUE`
del lado del Ingestor, `app.rabbitmq.queue` de este lado).

Importante: como el productor es Node/amqplib, el mensaje no trae el header
`__TypeId__` que Spring usa por default para elegir la clase Java al
deserializar. Por eso `RabbitMQConfig` configura el converter con
`TypePrecedence.INFERRED`, para que use el tipo del parámetro del listener
en vez de ese header.

## Pendientes (marcados como TODO en el código)

- [ ] Lógica real de detección de groserías (`ModerationServiceImpl.evaluateProfanityRule`)
- [ ] Integración con el modelo de IA de toxicidad (`ModerationServiceImpl.evaluateToxicityWithAi`)
- [ ] Fórmula real de combinación de scores (`ModerationServiceImpl.combineScores`)
- [ ] Ajustar `IncomingVerdictRequest`/`ChannelDecisionResponse` cuando exista el contrato OpenAPI de CoreApi (la API central)
- [ ] Reemplazar `RecentMessagesTracker` (en memoria) por Redis si el servicio corre en más de una instancia
- [ ] Definir quién ejecuta la acción final en Twitch (timeout/borrado) una vez que CoreApi la resuelve — el propio README del Ingestor lo marca como fase posterior
