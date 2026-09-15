package com.twitchspamdetector.moderationengine.controller;

import com.twitchspamdetector.moderationengine.dto.request.ChatMessageRequest;
import com.twitchspamdetector.moderationengine.dto.response.VerdictResponse;
import com.twitchspamdetector.moderationengine.service.ModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Via sincrona del contrato: POST /analyze.
 * La via asincrona equivalente es ChatMessageListener (RabbitMQ).
 */
@RestController
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @PostMapping("/analyze")
    public ResponseEntity<VerdictResponse> analyze(@Valid @RequestBody ChatMessageRequest message) {
        VerdictResponse verdict = moderationService.analyze(message);
        return ResponseEntity.ok(verdict);
    }
}
