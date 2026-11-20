package com.chx.claims.claim.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="claim_status_history")
public class ClaimStatusHistory {
    @Id private UUID id; @Enumerated(EnumType.STRING) private ClaimStatus fromStatus; @Enumerated(EnumType.STRING) private ClaimStatus toStatus;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;
    private UUID changedBy; private String note; private Instant changedAt;
    protected ClaimStatusHistory() { }
    ClaimStatusHistory(Claim claim, ClaimStatus from, ClaimStatus to, UUID by, String note, Instant at) { id=UUID.randomUUID(); this.claim=claim; fromStatus=from; toStatus=to; changedBy=by; this.note=note; changedAt=at; }
    public ClaimStatus getToStatus(){return toStatus;} public Instant getChangedAt(){return changedAt;}
}
