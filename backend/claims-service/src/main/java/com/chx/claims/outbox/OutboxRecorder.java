package com.chx.claims.outbox;

import com.chx.claims.outbox.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.*; import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OutboxRecorder {
    private final OutboxRepository repository; private final ObjectMapper mapper; private final Clock clock;
    public OutboxRecorder(OutboxRepository repository, ObjectMapper mapper, Clock clock){this.repository=repository;this.mapper=mapper;this.clock=clock;}
    public void record(String type, UUID aggregateId, ClaimEventPayload payload){
        try {
            UUID eventId=UUID.randomUUID(); Instant now=Instant.now(clock);
            var envelope=new EventEnvelope(eventId,type,1,now,aggregateId,payload);
            repository.save(new OutboxEvent(eventId,"Claim",aggregateId,type,mapper.writeValueAsString(envelope),now));
        } catch (JsonProcessingException e) { throw new IllegalStateException("Unable to serialize outbox event",e); }
    }
}
