package com.example;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.example.Config.*;

@Profile("server")
@Component
public class MyBackend {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RPC_REQUEST_QUEUE, concurrency = "2")
    public void processRequest(Message message) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        int number = Integer.parseInt(body);

        System.out.println(message);

        System.out.println("[Server] Received request: " + number);

        int result = fibonacci(number);
        System.out.println("[Server] Computed result: " + result);

        // Send response
        rabbitTemplate.convertAndSend(
                RPC_EXCHANGE,
                RPC_REPLY_QUEUE,
                String.valueOf(result),
                msg -> {
                    final String correlationId = message.getMessageProperties().getCorrelationId();
                    System.out.println("correlationId : "+ correlationId);
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                }
        );
    }

    private int fibonacci(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
