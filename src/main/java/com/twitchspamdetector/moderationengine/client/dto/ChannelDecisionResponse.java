package com.twitchspamdetector.moderationengine.client.dto;

import com.twitchspamdetector.moderationengine.enums.ModerationAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de la API central al recibir un IncomingVerdict: la accion final
 * ya resuelta contra la configuracion del canal.
 *
 * TODO: ajustar cuando tengas el contrato real de la API central.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDecisionResponse {

    private ModerationAction action;
}
