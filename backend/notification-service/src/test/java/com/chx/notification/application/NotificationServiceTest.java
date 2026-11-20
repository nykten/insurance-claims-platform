package com.chx.notification.application;

import com.chx.notification.api.EventEnvelope;
import com.chx.notification.domain.*;
import com.chx.notification.infra.*;
import java.time.*; import java.util.UUID;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension; import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock ProcessedEventRepository processed; @Mock SentNotificationRepository sent; @Mock JavaMailSender mail;
    @Test void duplicateEventSendsOnlyOneEmail(){
        UUID id=UUID.randomUUID(); var payload=new EventEnvelope.Payload("CHX-SG-1","alice@example.com","Alice","APPROVED","Approved",null);
        var event=new EventEnvelope(id,"ClaimDecided",1,Instant.now(),UUID.randomUUID(),payload); when(processed.existsById(id)).thenReturn(false,true);
        var service=new NotificationService(processed,sent,mail,new MessageTemplates(),Clock.systemUTC()); service.handle(event); service.handle(event);
        verify(mail,times(1)).send(any(org.springframework.mail.SimpleMailMessage.class)); verify(processed,times(1)).save(any()); verify(sent,times(1)).save(any());
    }
}
