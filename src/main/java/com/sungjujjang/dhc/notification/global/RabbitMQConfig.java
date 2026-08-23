package com.sungjujjang.dhc.notification.global;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue emailQueue(
            @Value("${rabbitmq.email.queue}") String queue,
            @Value("${rabbitmq.email.dlx}") String dlx,
            @Value("${rabbitmq.email.dlq}") String dlq
    ) {
        Map<String, Object> arguments = new HashMap<>();

        arguments.put("x-dead-letter-exchange", dlx);
        arguments.put("x-dead-letter-routing-key", dlq);

        return new Queue(
                queue,
                true,
                false,
                false,
                arguments
        );
    }

    @Bean
    public DirectExchange emailDlx(
            @Value("${rabbitmq.email.dlx}") String dlx
    ) {
        return new DirectExchange(dlx);
    }

    @Bean
    public Queue emailDlq(
            @Value("${rabbitmq.email.dlq}") String dlq
    ) {
        return new Queue(dlq);
    }

    @Bean
    public Binding emailDlqBinding(
            Queue emailDlq,
            DirectExchange emailDlx,
            @Value("${rabbitmq.email.dlq}") String routingKey
    ) {
        return BindingBuilder
                .bind(emailDlq)
                .to(emailDlx)
                .with(routingKey);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}