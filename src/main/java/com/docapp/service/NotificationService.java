package com.docapp.service;

import com.docapp.config.RabbitMQConfig;
import com.docapp.messaging.AppointmentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(AppointmentMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING,
                message
            );
            log.info("Notification published to RabbitMQ — appointmentId={} status={}",
                message.getAppointmentId(), message.getStatus());
        } catch (Exception e) {
            log.error("Failed to publish notification to RabbitMQ: {}", e.getMessage());
        }
    }
}
