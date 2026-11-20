package com.chx.claims.policy;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PolicyRepository extends JpaRepository<Policy, UUID> { Optional<Policy> findByIdAndHolderId(UUID id, UUID holderId); }
