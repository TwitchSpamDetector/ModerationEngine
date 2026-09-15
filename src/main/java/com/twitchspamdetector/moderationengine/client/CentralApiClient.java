package com.twitchspamdetector.moderationengine.client;

import com.twitchspamdetector.moderationengine.client.dto.ChannelDecisionResponse;
import com.twitchspamdetector.moderationengine.client.dto.IncomingVerdictRequest;
import com.twitchspamdetector.moderationengine.enums.ModerationAction;
import com.twitchspamdetector.moderationengine.exception.CentralApiUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Encapsula la llamada sincrona a la API central para resolver la accion final
 * de un veredicto, segun la configuracion del canal.
 *
 * Protegida con Resilience4j (ver application.yml, instancia "centralApi"):
 * - @Retry: reintenta fallas transitorias (red, 5xx) antes de darse por vencido.
 * - @CircuitBreaker: si la API central sigue fallando, abre el circuito y deja
 *   de intentarla por un rato (evita bombardearla mientras esta caida); el
 *   circuito envuelve al retry (circuit-breaker-aspect-order < retry-aspect-order),
 *   asi que en OPEN se va directo al fallback sin reintentar.
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

    @CircuitBreaker(name = "centralApi", fallbackMethod = "resolveActionFallback")
    @Retry(name = "centralApi")
    public ModerationAction resolveAction(String channelId, IncomingVerdictRequest incomingVerdict) {
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
    }

    /**
     * Se invoca cuando se agotan los reintentos o el circuito esta abierto.
     * La firma debe coincidir con resolveAction + el Throwable de la falla.
     * Mantiene el mismo contrato que antes: el llamador (REST o el listener
     * de RabbitMQ) sigue viendo un CentralApiUnavailableException.
     */
    private ModerationAction resolveActionFallback(String channelId, IncomingVerdictRequest incomingVerdict,
                                                   Throwable ex) {
        log.error("No se pudo obtener la decision final de la API central para channelId={} " +
                "(reintentos agotados o circuito abierto)", channelId, ex);
        throw new CentralApiUnavailableException(
                "No se pudo obtener la decision final de la API central", ex);
    }
}