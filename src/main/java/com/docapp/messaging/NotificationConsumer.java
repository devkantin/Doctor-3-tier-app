package com.docapp.messaging;

import com.docapp.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes appointment events from RabbitMQ (Amazon MQ in production).
 * In production this would call SES/SNS to send real emails/SMS.
 */
@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleNotification(AppointmentMessage message) {
        log.info("═══ Notification Received ═══");
        log.info("  Appointment ID : {}", message.getAppointmentId());
        log.info("  Patient        : {} <{}>", message.getPatientName(), message.getPatientEmail());
        log.info("  Doctor         : Dr. {} ({})", message.getDoctorName(), message.getDoctorSpecialization());
        log.info("  Date / Time    : {} at {}", message.getAppointmentDate(), message.getTimeSlot());
        log.info("  Status         : {}", message.getStatus());
        log.info("═════════════════════════════");

        // Simulate sending email (plug in SES / SendGrid here)
        sendEmail(message);
    }

    private void sendEmail(AppointmentMessage msg) {
        String subject = buildSubject(msg.getStatus(), msg.getAppointmentId());
        String body    = buildBody(msg);
        // In production: sesClient.sendEmail(from, msg.getPatientEmail(), subject, body);
        log.info("Email '{}' would be sent to {}", subject, msg.getPatientEmail());
    }

    private String buildSubject(String status, Long id) {
        return switch (status) {
            case "PENDING"    -> "Appointment #" + id + " received — pending confirmation";
            case "CONFIRMED"  -> "Appointment #" + id + " confirmed!";
            case "CANCELLED"  -> "Appointment #" + id + " has been cancelled";
            case "COMPLETED"  -> "Thank you for your visit — Appointment #" + id;
            default           -> "Appointment #" + id + " update: " + status;
        };
    }

    private String buildBody(AppointmentMessage msg) {
        return String.format("""
            Dear %s,

            Your appointment with Dr. %s (%s) on %s at %s is now %s.

            Regards,
            DocApp Team
            """,
            msg.getPatientName(), msg.getDoctorName(), msg.getDoctorSpecialization(),
            msg.getAppointmentDate(), msg.getTimeSlot(), msg.getStatus());
    }
}
