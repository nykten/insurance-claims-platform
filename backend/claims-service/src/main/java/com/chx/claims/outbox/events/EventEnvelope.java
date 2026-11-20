package com.chx.claims.outbox.events;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(UUID eventId, String eventType, int version, Instant occurredAt,
                            UUID aggregateId, Object payload) { }
