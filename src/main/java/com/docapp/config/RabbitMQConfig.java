package com.docapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE    = "appointment.notification";
    public static final String EXCHANGE = "doctor.exchange";
    public static final String ROUTING  = "appointment.notification";

    @Bean
    public Queue appointmentQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange doctorExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue appointmentQueue, TopicExchange doctorExchange) {
        return BindingBuilder.bind(appointmentQueue).to(doctorExchange).with(ROUTING);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
