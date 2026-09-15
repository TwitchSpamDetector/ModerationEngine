package com.twitchspamdetector.moderationengine.client;

import com.twitchspamdetector.moderationengine.client.dto.ChannelDecisionResponse;
import com.twitchspamdetector.moderationengine.client.dto.IncomingVerdictRequest;
import com.twitchspamdetector.moderationengine.enums.ModerationAction;
import com.twitchspamdetector.moderationengine.exception.CentralApiUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Encapsula la llamada sincrona a la API central para resolver la accion final
 * de un veredicto, segun la configuracion del canal.
 *
 * TODO: reemplazar la URL/paths y el shape de IncomingVerdictRequest/ChannelDecisionResponse
 * cuando tengas el contrato OpenAPI real de la API central.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CentralApiClient {

    private final RestClient restClient;

    @Value("${app.central-api.base-url}")
    private String centralApiBaseUrl;

    public ModerationAction resolveAction(String channelId, IncomingVerdictRequest incomingVerdict) {
        try {
            ChannelDecisionResponse decision = restClient.post()
                    .uri(centralApiBaseUrl + "/api/v1/channels/{channelId}/verdicts", channelId)
                    .body(incomingVerdict)
                    .retrieve()
                    .body(ChannelDecisionResponse.class);

            if (decision == null || decision.getAction() == null) {
                throw new CentralApiUnavailableException(
                        "La API central respondio sin una accion valida", null);
            }
            return decision.getAction();

        } catch (RestClientException ex) {
            log.error("No se pudo obtener la decision final de la API central para channelId={}",
                    channelId, ex);
            throw new CentralApiUnavailableException(
                    "No se pudo obtener la decision final de la API central", ex);
        }
    }
}
