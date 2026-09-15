package com.twitchspamdetector.moderationengine.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Formato estandar de error/excepcion acordado para todo el proyecto
 * (Ingestor, Motor de Moderacion y API central).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private int status;
    private String error;
    private String message;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String field;

    private String path;
    private OffsetDateTime timestamp;
}
