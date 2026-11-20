package com.chx.claims.outbox;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query(value="select * from outbox_events where status='PENDING' order by created_at for update skip locked limit 50", nativeQuery=true)
    List<OutboxEvent> lockPending();
}
