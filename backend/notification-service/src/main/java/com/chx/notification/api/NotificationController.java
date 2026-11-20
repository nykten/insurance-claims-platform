package com.chx.notification.api;
import com.chx.notification.domain.SentNotification; import com.chx.notification.infra.SentNotificationRepository; import java.util.List; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/notifications") public class NotificationController { private final SentNotificationRepository repo; public NotificationController(SentNotificationRepository r){repo=r;} @GetMapping public List<SentNotification> recent(){return repo.findAll();} }
