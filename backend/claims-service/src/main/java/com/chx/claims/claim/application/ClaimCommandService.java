package com.chx.claims.claim.application;

import com.chx.claims.claim.domain.Claim;
import com.chx.claims.claim.infra.ClaimRepository;
import com.chx.claims.common.error.ResourceNotFoundException;
import com.chx.claims.policy.*;
import com.chx.claims.identity.infra.UserRepository;
import com.chx.claims.outbox.OutboxRecorder;
import com.chx.claims.outbox.events.ClaimEventPayload;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

@Service public class ClaimCommandService {
 private final ClaimRepository claims; private final PolicyRepository policies; private final PolicyValidator validator; private final ClaimNumberGenerator numbers; private final Clock clock; private final UserRepository users; private final OutboxRecorder outbox;
 public ClaimCommandService(ClaimRepository c, PolicyRepository p, PolicyValidator v, ClaimNumberGenerator n, Clock clock, UserRepository users, OutboxRecorder outbox){claims=c;policies=p;validator=v;numbers=n;this.clock=clock;this.users=users;this.outbox=outbox;}
 @Transactional public Claim submit(UUID claimant, UUID policyId, ClaimType type, Market market, LocalDate date, String description, BigDecimal amount, String currency){Policy p=policies.findByIdAndHolderId(policyId,claimant).orElseThrow(()->new ResourceNotFoundException("Policy not found"));validator.validate(p,type,date,amount,clock);Claim c=claims.save(new Claim(numbers.next(market),policyId,claimant,type,market,date,description,amount,currency,clock));record("ClaimSubmitted",c,claimant,null,amount);return c;}
 @Transactional public Claim pickUp(UUID id, UUID officer){Claim c=get(id);c.pickUp(officer,clock);record("ClaimAssigned",c, c.getClaimantId(),"Assigned to officer",null);return c;}
 @Transactional public Claim requestInfo(UUID id, UUID officer, String question){Claim c=get(id);c.requestInfo(officer,question,clock);record("InfoRequested",c,c.getClaimantId(),question,null);return c;}
 @Transactional public Claim provideInfo(UUID id, UUID claimant, String answer){Claim c=get(id);if(!c.getClaimantId().equals(claimant))throw new ResourceNotFoundException("Claim not found");c.provideInfo(answer,clock);record("InfoProvided",c,claimant,answer,null);return c;}
 @Transactional public Claim approve(UUID id,UUID officer,BigDecimal amount){Claim c=get(id);c.approve(officer,amount,clock);record("ClaimDecided",c,c.getClaimantId(),"Approved",amount);return c;}
 @Transactional public Claim reject(UUID id,UUID officer,String reason){Claim c=get(id);c.reject(officer,reason,clock);record("ClaimDecided",c,c.getClaimantId(),reason,null);return c;}
 @Transactional public Claim settle(UUID id,UUID officer){Claim c=get(id);c.settle(officer,clock);record("ClaimDecided",c,c.getClaimantId(),"Settled",null);return c;}
 private void record(String type, Claim c, UUID claimant, String details, BigDecimal amount){var u=users.findById(claimant).orElse(null);outbox.record(type,c.getId(),new ClaimEventPayload(c.getClaimNumber(),u==null?null:u.getEmail(),u==null?null:u.getFullName(),c.getStatus().name(),details,amount));}
 private Claim get(UUID id){return claims.findById(id).orElseThrow(()->new ResourceNotFoundException("Claim not found"));}
}
