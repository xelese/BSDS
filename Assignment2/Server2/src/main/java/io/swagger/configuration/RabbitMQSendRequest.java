package io.swagger.configuration;

import javafx.util.Pair;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@EnableRabbit
public class RabbitMQSendRequest {

    private final AmqpTemplate rabbitMQTemplate;

    public RabbitMQSendRequest(AmqpTemplate rabbitMQTemplate) {
        this.rabbitMQTemplate = rabbitMQTemplate;
    }

    public void send(Map<String, Integer> tuple) {
        rabbitMQTemplate.convertAndSend("TestRabbitMQExchange", "TestRabbitMQ", tuple);
    }
}
