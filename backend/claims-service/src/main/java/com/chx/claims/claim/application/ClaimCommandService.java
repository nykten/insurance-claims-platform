package com.chx.claims.claim.application;

import com.chx.claims.claim.domain.Claim;
import com.chx.claims.claim.infra.ClaimRepository;
import com.chx.claims.common.error.ResourceNotFoundException;
import com.chx.claims.policy.*;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

@Service public class ClaimCommandService {
 private final ClaimRepository claims; private final PolicyRepository policies; private final PolicyValidator validator; private final ClaimNumberGenerator numbers; private final Clock clock;
 public ClaimCommandService(ClaimRepository c, PolicyRepository p, PolicyValidator v, ClaimNumberGenerator n, Clock clock){claims=c;policies=p;validator=v;numbers=n;this.clock=clock;}
 @Transactional public Claim submit(UUID claimant, UUID policyId, ClaimType type, Market market, LocalDate date, String description, BigDecimal amount, String currency){Policy p=policies.findByIdAndHolderId(policyId,claimant).orElseThrow(()->new ResourceNotFoundException("Policy not found"));validator.validate(p,type,date,amount,clock);return claims.save(new Claim(numbers.next(market),policyId,claimant,type,market,date,description,amount,currency,clock));}
 @Transactional public Claim pickUp(UUID id, UUID officer){Claim c=get(id);c.pickUp(officer,clock);return c;}
 @Transactional public Claim requestInfo(UUID id, UUID officer, String question){Claim c=get(id);c.requestInfo(officer,question,clock);return c;}
 @Transactional public Claim provideInfo(UUID id, UUID claimant, String answer){Claim c=get(id);if(!c.getClaimantId().equals(claimant))throw new ResourceNotFoundException("Claim not found");c.provideInfo(answer,clock);return c;}
 @Transactional public Claim approve(UUID id,UUID officer,BigDecimal amount){Claim c=get(id);c.approve(officer,amount,clock);return c;}
 @Transactional public Claim reject(UUID id,UUID officer,String reason){Claim c=get(id);c.reject(officer,reason,clock);return c;}
 @Transactional public Claim settle(UUID id,UUID officer){Claim c=get(id);c.settle(officer,clock);return c;}
 private Claim get(UUID id){return claims.findById(id).orElseThrow(()->new ResourceNotFoundException("Claim not found"));}
}
