package com.chx.claims.claim.domain;
import com.chx.claims.common.error.IllegalTransitionException; import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class ClaimStatusTest {
    @Test void validTransitions(){
        assertTrue(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.UNDER_REVIEW));
        assertTrue(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.INFO_REQUESTED));
        assertTrue(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.APPROVED));
        assertTrue(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.REJECTED));
        assertTrue(ClaimStatus.INFO_REQUESTED.canTransitionTo(ClaimStatus.UNDER_REVIEW));
        assertTrue(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.SETTLED));
    }
    @Test void invalidTransitionsThrow(){
        for(ClaimStatus from:ClaimStatus.values())
            for(ClaimStatus to:ClaimStatus.values())
                if(!from.canTransitionTo(to))
                    assertThrows(IllegalTransitionException.class,()->from.assertCanTransitionTo(to));
    }
}
