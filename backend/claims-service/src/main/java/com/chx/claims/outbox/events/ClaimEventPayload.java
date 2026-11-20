package com.chx.claims.outbox.events;

import java.math.BigDecimal;

public record ClaimEventPayload(String claimNumber, String claimantEmail, String claimantName,
                                String status, String details, BigDecimal amount) { }
