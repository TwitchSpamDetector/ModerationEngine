package com.twitchspamdetector.moderationengine.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Decision final sobre el mensaje, ya considerando la configuracion del canal
 * que devuelve la API central. Valores alineados con el enum "action" del contrato.
 */
public enum ModerationAction {

    NONE("none"),
    WARN("warn"),
    DELETE("delete"),
    TIMEOUT("timeout");

    private final String value;

    ModerationAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
