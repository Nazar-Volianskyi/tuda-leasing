package com.bobocode.tudaleasing.dto;

public record FeedbackRequest(
        String name,
        String email,
        String topic,
        String message
) {}