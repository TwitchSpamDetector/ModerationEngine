package com.twitchspamdetector.moderationengine.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config de mensajeria con el Ingestor.
 *
 * Confirmado contra el repo Ingestor (rabbitmq.service.ts /
 * moderationClient.service.ts): publica con `channel.sendToQueue(...)` sobre
 * el exchange por defecto de RabbitMQ, directo a una cola fija
 * ("chat-messages" por default, configurable con MODERATION_QUEUE de su
 * lado). No hay exchange propio ni routing key que declarar aqui: solo
 * necesitamos una cola con el mismo nombre, durable, para que el mensaje que
 * el Ingestor ya publica llegue a este listener.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Bean
    public Queue chatMessagesQueue() {
        return new Queue(queueName, true);
    }

    /**
     * El Ingestor (Node/amqplib) publica JSON plano y no manda el header
     * __TypeId__ que Spring usa por default para saber a que clase Java
     * deserializar. Con TypePrecedence.INFERRED, si ese header no viene,
     * el converter usa el tipo del parametro del metodo @RabbitListener
     * (ChatMessageRequest) en vez de fallar con MessageConversionException.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
