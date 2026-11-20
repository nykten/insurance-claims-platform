package com.chx.claims.outbox;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "outbox_events")
public class OutboxEvent {
    @Id private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String eventType;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String payload;
    private String status;
    private Instant createdAt;
    private Instant publishedAt;
    protected OutboxEvent() { }
    public OutboxEvent(UUID id, String aggregateType, UUID aggregateId, String eventType, String payload, Instant createdAt) {
        this.id=id; this.aggregateType=aggregateType; this.aggregateId=aggregateId; this.eventType=eventType;
        this.payload=payload; this.status="PENDING"; this.createdAt=createdAt;
    }
    public UUID getId(){return id;} public UUID getAggregateId(){return aggregateId;} public String getEventType(){return eventType;}
    public String getPayload(){return payload;} public String getStatus(){return status;} public void markPublished(Instant when){status="PUBLISHED";publishedAt=when;}
}
