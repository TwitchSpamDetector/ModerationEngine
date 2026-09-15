package com.twitchspamdetector.moderationengine.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Motivos por los que un mensaje fue marcado como spam/toxico.
 * Los valores deben coincidir EXACTAMENTE con el enum "reasons" del contrato OpenAPI.
 */
public enum ReasonCode {

    REPETIDO_3_VECES("repetido_3_veces"),
    GROSERIA_DETECTADA("groseria_detectada"),
    TOXICIDAD_ALTA("toxicidad_alta");

    private final String value;

    ReasonCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
