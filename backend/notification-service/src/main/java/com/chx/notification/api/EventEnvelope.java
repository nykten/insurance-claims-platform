package com.chx.notification.api;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; import java.time.Instant; import java.util.UUID;
@JsonIgnoreProperties(ignoreUnknown=true) public record EventEnvelope(UUID eventId,String eventType,int version,Instant occurredAt,UUID aggregateId,Payload payload){
 @JsonIgnoreProperties(ignoreUnknown=true) public record Payload(String claimNumber,String claimantEmail,String claimantName,String status,String details,java.math.BigDecimal amount){}
}
