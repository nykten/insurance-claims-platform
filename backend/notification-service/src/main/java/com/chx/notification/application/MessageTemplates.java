package com.chx.notification.application;
import com.chx.notification.api.EventEnvelope; import org.springframework.stereotype.Component;
@Component public class MessageTemplates { public String subject(EventEnvelope e){return "CHX claim update: "+e.payload().claimNumber();} public String body(EventEnvelope e){var p=e.payload();return "Hello %s,\n\nClaim %s is now %s.\n%s\n\nCHX Insurance".formatted(p.claimantName(),p.claimNumber(),p.status(),p.details()==null?"":p.details());} }
