package com.chx.claims.outbox;

import java.time.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private final OutboxRepository repository; private final KafkaTemplate<String,String> kafka; private final Clock clock;
    public OutboxPublisher(OutboxRepository repository,KafkaTemplate<String,String> kafka,Clock clock){this.repository=repository;this.kafka=kafka;this.clock=clock;}
    @Scheduled(fixedDelay=1000)
    @Transactional
    public void publish(){ for(var event: repository.lockPending()){ kafka.send("claim-events",event.getAggregateId().toString(),event.getPayload()); event.markPublished(Instant.now(clock)); } }
}
