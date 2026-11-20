package com.chx.claims.policy;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name="policies")
public class Policy {
    @Id private UUID id; @Column(nullable=false, unique=true) private String policyNumber;
    @Column(nullable=false) private UUID holderId; @Enumerated(EnumType.STRING) private ClaimType claimType;
    @Enumerated(EnumType.STRING) private Market market; private BigDecimal coverageLimit; private String currency;
    private LocalDate validFrom, validTo; @Enumerated(EnumType.STRING) private Status status;
    public enum Status { ACTIVE, EXPIRED, CANCELLED }
    protected Policy() { }
    public UUID getId(){return id;} public UUID getHolderId(){return holderId;} public ClaimType getClaimType(){return claimType;}
    public BigDecimal getCoverageLimit(){return coverageLimit;} public LocalDate getValidFrom(){return validFrom;} public LocalDate getValidTo(){return validTo;}
    public Status getStatus(){return status;}
}
