package com.ltfullstack.notificationservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventConsumer {

    @RetryableTopic(
            attempts = "4", // 3 topic retry + 1 topic DLQ
            backoff = @Backoff(delay = 1000, multiplier = 2),
            autoCreateTopics = "true",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            include = {RetryException.class, RuntimeException.class}
    )

    @KafkaListener(topics = "test", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message){
        log.info("Received Message: " + message);

        //processing message
//        throw new RuntimeException("Error test");
    }
    @DltHandler
    void processDltMessage(@Payload String message){
        log.info("DLT receive message: " + message);
    }

}
