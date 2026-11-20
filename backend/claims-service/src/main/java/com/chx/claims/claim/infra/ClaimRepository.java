package com.chx.claims.claim.infra;
import com.chx.claims.claim.domain.Claim;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClaimRepository extends JpaRepository<Claim, UUID> { List<Claim> findByClaimantId(UUID claimantId); List<Claim> findByAssignedOfficerId(UUID officerId); }
