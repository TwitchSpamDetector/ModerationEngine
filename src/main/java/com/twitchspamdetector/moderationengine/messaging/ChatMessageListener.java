package com.twitchspamdetector.moderationengine.messaging;

import com.twitchspamdetector.moderationengine.dto.request.ChatMessageRequest;
import com.twitchspamdetector.moderationengine.dto.response.VerdictResponse;
import com.twitchspamdetector.moderationengine.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consume los ChatMessage que el Ingestor publica con `channel.sendToQueue(...)`
 * (cola "chat-messages" del exchange por defecto, ver RabbitMQConfig) y corre
 * la misma logica de analisis que el endpoint POST /analyze.
 *
 * moderationService.analyze() ya se encarga de enviar el veredicto (como
 * IncomingVerdict) a la API central, asi que no hay que publicarlo en otra
 * cola desde aqui. Lo unico pendiente (marcado como fase posterior en el
 * README del Ingestor) es quien ejecuta la accion resultante en Twitch
 * (timeout/borrado) una vez que la API central la resuelve.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageListener {

    private final ModerationService moderationService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void onChatMessage(ChatMessageRequest message) {
        VerdictResponse verdict = moderationService.analyze(message);
        log.info("Veredicto generado para messageId={} channelId={} action={}",
                verdict.getMessageId(), verdict.getChannelId(), verdict.getAction());
    }
}
