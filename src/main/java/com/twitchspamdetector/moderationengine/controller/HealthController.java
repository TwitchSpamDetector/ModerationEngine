package com.twitchspamdetector.moderationengine.controller;

import com.twitchspamdetector.moderationengine.dto.response.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder().build();
    }
}
