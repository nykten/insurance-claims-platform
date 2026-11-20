package com.chx.claims.outbox;

import java.time.*; import java.util.List; import java.util.UUID;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension; import org.springframework.kafka.core.KafkaTemplate;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {
    @Mock OutboxRepository repository; @Mock KafkaTemplate<String,String> kafka;
    @Test void publishesPendingEventsAndMarksThemPublished(){
        UUID id=UUID.randomUUID(); var event=new OutboxEvent(UUID.randomUUID(),"Claim",id,"ClaimSubmitted","{}",Instant.now()); when(repository.lockPending()).thenReturn(List.of(event));
        new OutboxPublisher(repository,kafka,Clock.systemUTC()).publish();
        verify(kafka).send("claim-events",id.toString(),"{}"); assertEquals("PUBLISHED",event.getStatus());
    }
}
