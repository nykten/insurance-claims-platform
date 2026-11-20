package com.chx.claims.assignment.application;
import com.chx.claims.claim.domain.Claim; import com.chx.claims.claim.infra.ClaimRepository; import java.util.*; import org.springframework.stereotype.Service;
@Service public class AssignmentService { private final ClaimRepository claims; public AssignmentService(ClaimRepository c){claims=c;} public List<Claim> queue(){return claims.findAll().stream().filter(c->c.getStatus().name().equals("SUBMITTED")).toList();} public List<Claim> mine(UUID officer){return claims.findByAssignedOfficerId(officer);} }
