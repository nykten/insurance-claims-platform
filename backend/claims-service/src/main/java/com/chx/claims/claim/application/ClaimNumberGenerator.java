package com.chx.claims.claim.application;
import com.chx.claims.claim.infra.ClaimRepository;
import com.chx.claims.policy.Market;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
@Component public class ClaimNumberGenerator {
    private final AtomicLong sequence;

    public ClaimNumberGenerator(ClaimRepository claims) {
        sequence = new AtomicLong(Math.max(100000, claims.maxClaimSequence()));
    }

    public String next(Market market){return "CHX-%s-%06d".formatted(market,sequence.incrementAndGet());}
}
