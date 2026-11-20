package com.chx.claims.claim.application;
import com.chx.claims.policy.Market;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
@Component public class ClaimNumberGenerator { private final AtomicLong sequence=new AtomicLong(100000); public String next(Market market){return "CHX-%s-%06d".formatted(market,sequence.incrementAndGet());} }
