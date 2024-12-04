package com.example;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.example.Config.RPC_EXCHANGE;
import static com.example.Config.RPC_REPLY_QUEUE;
import static com.example.Config.RPC_REQUEST_KEY;

@Profile("client")
@Component
public class MyRequestor {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${counter}")
    int number ;
    @Scheduled(fixedDelay = 2000, initialDelay = 500)
    public int sendRpcRequest() {
        Integer response = (Integer) rabbitTemplate.convertSendAndReceive(
                RPC_EXCHANGE,
                RPC_REQUEST_KEY,
                String.valueOf(number++));

        if (response != null) {
            System.out.println("[Client] Received response: " + response);
        } else {
            System.err.println("[Client] Response is null!");
        }
        return response == null ? -1 : response;
    }
}
