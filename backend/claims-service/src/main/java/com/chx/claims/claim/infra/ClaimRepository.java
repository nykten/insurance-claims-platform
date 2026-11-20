package com.chx.claims.claim.infra;
import com.chx.claims.claim.domain.Claim;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByClaimantId(UUID claimantId);
    List<Claim> findByAssignedOfficerId(UUID officerId);
    @Query(value = "select coalesce(max(cast(substring(claim_number from '[0-9]+$') as bigint)), 0) from claims", nativeQuery = true)
    long maxClaimSequence();
}
