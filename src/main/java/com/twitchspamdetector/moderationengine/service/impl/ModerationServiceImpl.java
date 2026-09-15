package com.twitchspamdetector.moderationengine.service.impl;

import com.twitchspamdetector.moderationengine.client.CentralApiClient;
import com.twitchspamdetector.moderationengine.client.dto.IncomingVerdictRequest;
import com.twitchspamdetector.moderationengine.dto.request.ChatMessageRequest;
import com.twitchspamdetector.moderationengine.dto.response.VerdictResponse;
import com.twitchspamdetector.moderationengine.enums.ModerationAction;
import com.twitchspamdetector.moderationengine.enums.ReasonCode;
import com.twitchspamdetector.moderationengine.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private static final int REPEAT_THRESHOLD = 3;

    private final RecentMessagesTracker recentMessagesTracker;
    private final CentralApiClient centralApiClient;

    @Override
    public VerdictResponse analyze(ChatMessageRequest message) {
        List<ReasonCode> reasons = new ArrayList<>();

        double ruleScore = evaluateRepeatedMessageRule(message, reasons);
        double profanityScore = evaluateProfanityRule(message, reasons);
        double toxicityScore = evaluateToxicityWithAi(message, reasons);

        double spamScore = combineScores(ruleScore, profanityScore, toxicityScore);
        boolean isSpam = spamScore >= 0.5;

        IncomingVerdictRequest incomingVerdict = IncomingVerdictRequest.builder()
                .messageId(message.getMessageId())
                .userId(message.getUserId())
                .spamScore(spamScore)
                .isSpam(isSpam)
                .reasons(reasons)
                .build();

        ModerationAction action = centralApiClient.resolveAction(message.getChannelId(), incomingVerdict);

        return VerdictResponse.builder()
                .messageId(message.getMessageId())
                .channelId(message.getChannelId())
                .userId(message.getUserId())
                .spamScore(spamScore)
                .isSpam(isSpam)
                .reasons(reasons)
                .action(action)
                .build();
    }

    private double evaluateRepeatedMessageRule(ChatMessageRequest message, List<ReasonCode> reasons) {
        int repeatCount = recentMessagesTracker.registerAndCountRepeats(
                message.getChannelId(), message.getUserId(), message.getText());

        if (repeatCount >= REPEAT_THRESHOLD) {
            reasons.add(ReasonCode.REPETIDO_3_VECES);
            return 1.0;
        }
        return 0.0;
    }

    private double evaluateProfanityRule(ChatMessageRequest message, List<ReasonCode> reasons) {
        // TODO: reemplazar por una lista/regex real de groserias (o un servicio dedicado).
        boolean containsProfanity = false;

        if (containsProfanity) {
            reasons.add(ReasonCode.GROSERIA_DETECTADA);
            return 1.0;
        }
        return 0.0;
    }

    private double evaluateToxicityWithAi(ChatMessageRequest message, List<ReasonCode> reasons) {
        // TODO: aqui va la llamada al modelo de IA de toxicidad mencionado en el
        // contrato (modelo propio, API externa, etc.). Por ahora devuelve neutro.
        double toxicityScore = 0.0;

        if (toxicityScore >= 0.7) {
            reasons.add(ReasonCode.TOXICIDAD_ALTA);
        }
        return toxicityScore;
    }

    private double combineScores(double ruleScore, double profanityScore, double toxicityScore) {
        // TODO: definir la formula real de combinacion (pesos por senal, etc.).
        double combined = Math.max(Math.max(ruleScore, profanityScore), toxicityScore);
        return Math.min(combined, 1.0);
    }
}
