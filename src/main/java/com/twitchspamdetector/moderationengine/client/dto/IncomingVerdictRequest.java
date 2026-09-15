package com.twitchspamdetector.moderationengine.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twitchspamdetector.moderationengine.enums.ReasonCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomingVerdictRequest {

    private UUID messageId;
    private String userId;
    private double spamScore;

    @Getter(AccessLevel.NONE)
    private boolean isSpam;

    private List<ReasonCode> reasons;

    @JsonProperty("isSpam")
    public boolean isSpam() {
        return isSpam;
    }
}