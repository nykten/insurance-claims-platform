package com.chx.claims.policy;

import java.math.BigDecimal;
import java.time.*;
import org.springframework.stereotype.Component;

@Component
public class PolicyValidator {
    public void validate(Policy p, ClaimType type, LocalDate incidentDate, BigDecimal amount, Clock clock) {
        if (p.getStatus()!=Policy.Status.ACTIVE) throw new IllegalArgumentException("Policy is not active");
        if (incidentDate.isAfter(LocalDate.now(clock)) || incidentDate.isBefore(p.getValidFrom()) || incidentDate.isAfter(p.getValidTo())) throw new IllegalArgumentException("Incident date is outside policy coverage");
        if (p.getClaimType()!=type) throw new IllegalArgumentException("Claim type does not match policy");
        if (amount.signum()<=0 || amount.compareTo(p.getCoverageLimit())>0) throw new IllegalArgumentException("Claimed amount is outside coverage");
    }
}
