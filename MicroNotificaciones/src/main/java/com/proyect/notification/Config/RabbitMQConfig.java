package com.proyect.notification.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // ========== COLAS PRINCIPALES ==========
    public static final String QUEUE_NOTIFICACIONES = "notificaciones.usuario.queue";
    public static final String EXCHANGE_NAME = "mascotas.exchange";
    public static final String ROUTING_KEY = "mascota.evento.*";

    // ========== DEAD LETTER (DLX/DLQ) ==========
    public static final String DLX_EXCHANGE = "mascotas.dlx";
    public static final String DLQ_QUEUE = "notificaciones.usuario.dlq";
    public static final String DLQ_ROUTING_KEY = "mascota.evento.dlq";

    // ========== POLITICAS DE RETENCION ==========
    private static final int MESSAGE_TTL = 86400000; // 24 horas en ms
    private static final int MAX_QUEUE_LENGTH = 10000;

    @Bean
    public Queue notificacionesQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", DLQ_ROUTING_KEY);
        args.put("x-message-ttl", MESSAGE_TTL);
        args.put("x-max-length", MAX_QUEUE_LENGTH);
        args.put("x-overflow", "reject-publish-dlx");

        return new Queue(QUEUE_NOTIFICACIONES, true, false, false, args);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ_QUEUE, true);
    }

    @Bean
    public TopicExchange mascotasExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange mascotasExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(mascotasExchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingDlq(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }

    // Convertidor para la cola principal
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // NUEVO: ContainerFactory exclusivo para la DLQ (sin convertidor JSON)
    @Bean
    public SimpleRabbitListenerContainerFactory dlqContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // Usamos SimpleMessageConverter para que NO intente deserializar con __TypeId__
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }
}