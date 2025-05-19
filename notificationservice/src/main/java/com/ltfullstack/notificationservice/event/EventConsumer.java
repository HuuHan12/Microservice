package com.ltfullstack.notificationservice.event;

import com.ltfullstack.commanservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventConsumer {

    @Autowired
    private EmailService emailService;

    @RetryableTopic(
            attempts = "4", // 3 topic retry + 1 topic DLQ
            backoff = @Backoff(delay = 1000, multiplier = 2),
            autoCreateTopics = "true",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            include = {RetryException.class, RuntimeException.class}
    )

    @KafkaListener(topics = "test", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info("Received Message: " + message);

        //processing message
//        throw new RuntimeException("Error test");
    }

    @DltHandler
    void processDltMessage(@Payload String message) {
        log.info("DLT receive message: " + message);
    }

    @KafkaListener(topics = "testEmail", containerFactory = "kafkaListenerContainerFactory")
    public void testEmail(String message) {
        log.info("Email receive message: " + message);

        String template = "<div>\n" +
                "     <h1>Welcome, %s!</h1>\n" +
                "      <p> Thank you for reading. We're excited to have you on board.</p>\n" +
                "      <p> Your username is:  <strong>%s</strong></p>\n" +
                "</div>";

        String filledMessage = String.format(template, "Leo Young", message);


        emailService.sendMail(message, "Thank you", filledMessage, true, null);

    }

    @KafkaListener(topics = "emailTemplate", containerFactory = "kafkaListenerContainerFactory")
    public void emailTemplate(String message) {
        log.info("Email receive message: " + message);

        Map<String, Object> placeholder = new HashMap<>();
        placeholder.put("name", "Leo Young");

        emailService.sendMailWithTemplate(message, "Welcome to Halloween", "emailTemplate.ftl", placeholder, null);
    }


}
