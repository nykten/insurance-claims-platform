package com.chx.claims.claim.domain;

import com.chx.claims.common.error.IllegalTransitionException;
import java.util.*;

public enum ClaimStatus {
    SUBMITTED, UNDER_REVIEW, INFO_REQUESTED, APPROVED, REJECTED, SETTLED;
    private static final Map<ClaimStatus, Set<ClaimStatus>> ALLOWED = Map.of(
        SUBMITTED, Set.of(UNDER_REVIEW), UNDER_REVIEW, Set.of(INFO_REQUESTED, APPROVED, REJECTED),
        INFO_REQUESTED, Set.of(UNDER_REVIEW), APPROVED, Set.of(SETTLED), REJECTED, Set.of(), SETTLED, Set.of());
    public void assertCanTransitionTo(ClaimStatus target) { if (!ALLOWED.get(this).contains(target)) throw new IllegalTransitionException("Cannot transition claim from %s to %s".formatted(this,target)); }
    public boolean canTransitionTo(ClaimStatus target) { return ALLOWED.get(this).contains(target); }
}
