package com.chx.notification.config; import java.time.Clock; import org.springframework.context.annotation.*; @Configuration public class ClockConfig {@Bean Clock clock(){return Clock.systemUTC();}}
