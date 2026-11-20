package com.chx.claims.claim.domain;

import com.chx.claims.policy.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "jsonb")
private String incidentDetails;

@Entity @Table(name="claims")
public class Claim {
    @Id private UUID id; @Column(nullable=false, unique=true) private String claimNumber; private UUID policyId, claimantId, assignedOfficerId;
    @Enumerated(EnumType.STRING) private ClaimType claimType; @Enumerated(EnumType.STRING) private Market market; @Enumerated(EnumType.STRING) private ClaimStatus status;
    private LocalDate incidentDate; private String description; @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition="jsonb") private String incidentDetails; private BigDecimal claimedAmount, reserveAmount, approvedAmount; private String rejectionReason, currency;
    @Version private long version; private Instant createdAt, updatedAt;
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true) @JoinColumn(name="claim_id") private List<ClaimStatusHistory> statusHistory=new ArrayList<>();
    @OneToMany(mappedBy="claim", cascade=CascadeType.ALL, orphanRemoval=true) private List<InfoRequest> infoRequests=new ArrayList<>();
    protected Claim() { }
    public Claim(String number, UUID policyId, UUID claimantId, ClaimType type, Market market, LocalDate date, String description, BigDecimal amount, String currency, Clock clock) { id=UUID.randomUUID(); claimNumber=number; this.policyId=policyId; this.claimantId=claimantId; claimType=type; this.market=market; incidentDate=date; this.description=description; claimedAmount=amount; this.currency=currency; status=ClaimStatus.SUBMITTED; createdAt=updatedAt=Instant.now(clock); }
    public void pickUp(UUID officer, Clock clock){ transition(ClaimStatus.UNDER_REVIEW, officer, null, clock); assignedOfficerId=officer; }
    public void requestInfo(UUID officer, String question, Clock clock){ requireAssigned(officer); transition(ClaimStatus.INFO_REQUESTED, officer, question, clock); infoRequests.add(new InfoRequest(this, officer, question, Instant.now(clock))); }
    public void provideInfo(String answer, Clock clock){ if(status!=ClaimStatus.INFO_REQUESTED) throw new IllegalStateException("No open information request"); infoRequests.stream().filter(r->true).reduce((a,b)->b).ifPresent(r->r.respond(answer, Instant.now(clock))); transition(ClaimStatus.UNDER_REVIEW, claimantId, answer, clock); }
    public void approve(UUID officer, BigDecimal amount, Clock clock){ requireAssigned(officer); if(amount.signum()<0 || amount.compareTo(claimedAmount)>0) throw new IllegalArgumentException("Approved amount is invalid"); approvedAmount=amount; transition(ClaimStatus.APPROVED, officer, null, clock); }
    public void reject(UUID officer, String reason, Clock clock){ requireAssigned(officer); if(reason==null||reason.isBlank()) throw new IllegalArgumentException("Rejection reason is required"); rejectionReason=reason; transition(ClaimStatus.REJECTED, officer, reason, clock); }
    public void settle(UUID officer, Clock clock){ requireAssigned(officer); transition(ClaimStatus.SETTLED, officer, null, clock); }
    private void requireAssigned(UUID officer){ if(!Objects.equals(assignedOfficerId, officer)) throw new IllegalArgumentException("Claim is not assigned to this officer"); }
    private void transition(ClaimStatus target, UUID by, String note, Clock clock){ status.assertCanTransitionTo(target); statusHistory.add(new ClaimStatusHistory(status,target,by,note,Instant.now(clock))); status=target; updatedAt=Instant.now(clock); }
    public UUID getId(){return id;} public UUID getClaimantId(){return claimantId;} public UUID getAssignedOfficerId(){return assignedOfficerId;} public ClaimStatus getStatus(){return status;} public String getClaimNumber(){return claimNumber;} public BigDecimal getClaimedAmount(){return claimedAmount;} public BigDecimal getApprovedAmount(){return approvedAmount;} public List<ClaimStatusHistory> getStatusHistory(){return List.copyOf(statusHistory);} public List<InfoRequest> getInfoRequests(){return List.copyOf(infoRequests);}
}
