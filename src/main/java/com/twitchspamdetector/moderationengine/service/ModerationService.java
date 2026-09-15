package com.twitchspamdetector.moderationengine.service;

import com.twitchspamdetector.moderationengine.dto.request.ChatMessageRequest;
import com.twitchspamdetector.moderationengine.dto.response.VerdictResponse;

public interface ModerationService {

    /**
     * Analiza un mensaje (reglas + IA de toxicidad), consulta a la API central
     * segun la configuracion del canal y devuelve el veredicto final.
     */
    VerdictResponse analyze(ChatMessageRequest message);
}
