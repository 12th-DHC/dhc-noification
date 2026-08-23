package com.sungjujjang.dhc.notification.domain.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendEmail(EmailQueueDTO message) throws MessagingException {
        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MM/dd"));

        String result;
        String templateName;

        Context context = new Context();
        MimeMessage email = emailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(email, false, "UTF-8");

        if (message.passed()) {
            result = "통과";
            templateName = "email/cleaning-passed";
        } else {
            result = "미통과 (" + message.reason() + ")";
            context.setVariable("reason", message.reason());
            templateName = "email/cleaning-unpassed";
        }

        context.setVariable("name", message.name());
        context.setVariable("room", message.room());
        context.setVariable("passed", message.passed());
        context.setVariable("date", date);

        String html = templateEngine.process(templateName, context);

        helper.setFrom(senderEmail);
        helper.setTo(message.email());
        helper.setSubject(date + " DHC 청소 알림 결과 안내 - " + result);
        helper.setText(html, true);

        emailSender.send(email);
        log.info("전송에 성공하였습니다. {}({}호)", message.name(), message.room());
    }
}
