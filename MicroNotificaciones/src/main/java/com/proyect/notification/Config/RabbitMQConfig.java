package com.proyect.notification.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NOTIFICACIONES = "notificaciones.usuario.queue";
    public static final String EXCHANGE_NAME = "mascotas.exchange";
    public static final String ROUTING_KEY = "mascota.evento.*";

    @Bean
    public Queue notificacionesQueue() {
        return new Queue(QUEUE_NOTIFICACIONES, true);
    }

    @Bean
    public TopicExchange mascotasExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange mascotasExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(mascotasExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}