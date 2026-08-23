package com.sungjujjang.dhc.notification.domain.email;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Consumer {
    private final EmailSenderService emailSenderService;

    @RabbitListener(queues = "${rabbitmq.email.queue}")
    public void consume(EmailQueueDTO message) throws MessagingException {
        emailSenderService.sendEmail(message);
    }
}