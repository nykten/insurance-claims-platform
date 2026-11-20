package com.chx.claims.claim.api.dto;
import com.chx.claims.policy.*; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record SubmitClaimRequest(@NotNull UUID policyId,@NotNull ClaimType claimType,@NotNull Market market,@NotNull LocalDate incidentDate,@NotBlank String description,@NotNull @Positive BigDecimal claimedAmount,@NotBlank String currency) { }
