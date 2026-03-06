package com.openlms.api.classroom.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.openlms.api.commons.events.EnrollmentCreatedEvent;
import com.openlms.api.commons.utils.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "openlms.enrollment.created", groupId = "openlms-notif")
    public void onEnrollmentCreated(EnrollmentCreatedEvent event) {
        try {
            emailService.sendWelcomeEmail(event.studentEmail(), event.classTitle());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", event.studentEmail(), e);
        }
    }
}
