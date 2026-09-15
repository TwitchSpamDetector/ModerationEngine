package com.twitchspamdetector.moderationengine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Espejo del schema ChatMessage del contrato OpenAPI.
 * Es el mismo payload que llega por POST /analyze y por el mensaje de RabbitMQ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    @NotNull(message = "messageId es requerido")
    private UUID messageId;

    @NotBlank(message = "channelId es requerido")
    private String channelId;

    @NotBlank(message = "userId es requerido")
    private String userId;

    @NotBlank(message = "username es requerido")
    private String username;

    @NotBlank(message = "text no puede estar vacio")
    private String text;

    @NotNull(message = "timestamp es requerido")
    private OffsetDateTime timestamp;
}
