package com.chx.notification.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="processed_events") public class ProcessedEvent { @Id private UUID eventId; private String eventType; private Instant processedAt; protected ProcessedEvent(){} public ProcessedEvent(UUID id,String type,Instant at){eventId=id;eventType=type;processedAt=at;} }
