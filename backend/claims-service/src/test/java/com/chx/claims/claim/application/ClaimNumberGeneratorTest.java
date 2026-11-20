package com.chx.claims.claim.application;

import com.chx.claims.claim.infra.ClaimRepository;
import com.chx.claims.policy.Market;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClaimNumberGeneratorTest {

    @Test
    void startsAfterPersistedSequence() {
        ClaimRepository claims = mock(ClaimRepository.class);
        when(claims.maxClaimSequence()).thenReturn(100123L);

        ClaimNumberGenerator generator = new ClaimNumberGenerator(claims);

        assertEquals("CHX-SG-100124", generator.next(Market.SG));
    }
}
