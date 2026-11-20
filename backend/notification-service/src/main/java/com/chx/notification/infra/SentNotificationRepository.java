package com.chx.notification.infra;
import com.chx.notification.domain.SentNotification; import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface SentNotificationRepository extends JpaRepository<SentNotification,UUID>{}
