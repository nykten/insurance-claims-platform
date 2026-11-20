package com.chx.claims.claim.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="info_requests")
public class InfoRequest {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;
    private UUID requestedBy; private String question; private String response; private Instant requestedAt; private Instant respondedAt;
    protected InfoRequest() { }
    public InfoRequest(Claim claim, UUID requestedBy, String question, Instant now) { id=UUID.randomUUID(); this.claim=claim; this.requestedBy=requestedBy; this.question=question; requestedAt=now; }
    public void respond(String answer, Instant now) { if (response != null) throw new IllegalStateException("Info request already answered"); response=answer; respondedAt=now; }
}
