package com.chx.claims.outbox;

import com.chx.claims.outbox.events.ClaimEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal; import java.time.Clock; import java.time.Instant; import java.time.ZoneOffset; import java.util.UUID;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.ArgumentCaptor; import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRecorderTest {
    @Mock OutboxRepository repository;
    @Test void recordsSelfContainedEnvelope(){
        var recorder=new OutboxRecorder(repository,new ObjectMapper().findAndRegisterModules(),Clock.fixed(Instant.parse("2026-11-21T00:00:00Z"),ZoneOffset.UTC));
        UUID claimId=UUID.randomUUID(); recorder.record("ClaimSubmitted",claimId,new ClaimEventPayload("CHX-SG-1","alice@example.com","Alice","SUBMITTED","Submitted",BigDecimal.TEN));
        var captor=ArgumentCaptor.forClass(OutboxEvent.class); verify(repository).save(captor.capture()); var event=captor.getValue();
        assertEquals("PENDING",event.getStatus()); assertEquals(claimId,event.getAggregateId()); assertTrue(event.getPayload().contains("alice@example.com")); assertTrue(event.getPayload().contains("ClaimSubmitted"));
    }
}
