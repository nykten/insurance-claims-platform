package com.chx.notification.infra;
import com.chx.notification.domain.ProcessedEvent; import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent,UUID>{}
