package com.example;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.Config.RPC_EXCHANGE;
import static com.example.Config.RPC_REPLY_QUEUE;

@Profile("client")
@Component
public class MyRequestor {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    int number = 5;
    @Scheduled(fixedDelay = 30000, initialDelay = 500)
    public int sendRpcRequest() {
        String correlationId = UUID.randomUUID().toString();
        System.out.println("[Client] Sending request with CorrelationId: " + correlationId);

        Integer response = (Integer) rabbitTemplate.convertSendAndReceive(
                RPC_EXCHANGE,
                "rpc.request.key",
                String.valueOf(number++),
                message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
//                    message.getMessageProperties().setReplyTo(RPC_REPLY_QUEUE); //uncomment if template.setReplyAddress(RPC_REPLY_QUEUE); is commented
                    System.out.println(message);
                    return message;
                }
        );

        if (response != null) {
            System.out.println("[Client] Received response: " + response);
        } else {
            System.err.println("[Client] Response is null!");
        }
        return response == null ? -1 : response;
    }
}
