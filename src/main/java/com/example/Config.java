package com.example;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Config {

    public static final String RPC_EXCHANGE = "rpc.topic.exchange";
    public static final String RPC_REQUEST_QUEUE = "rpc.request.queue";
    public static final String RPC_REPLY_QUEUE = "rpc.reply.queue";

    @Bean
    public TopicExchange rpcExchange() {
        return new TopicExchange(RPC_EXCHANGE);
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(RPC_REQUEST_QUEUE);
    }

    @Bean
    public Queue replyQueue() {
        return new Queue(RPC_REPLY_QUEUE);
    }

    @Bean
    public Binding requestBinding(Queue requestQueue, TopicExchange rpcExchange) {
        return BindingBuilder.bind(requestQueue).to(rpcExchange).with("rpc.request.#");
    }

    @Profile("client")
    private static class ServerConfig {
        @Bean
        RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setReplyAddress(RPC_REPLY_QUEUE);
            template.setReplyTimeout(6000);
            template.setMessageConverter(new SimpleMessageConverter()); //Change 1 for NumberFormatException
            return template;
        }

        @Bean
        SimpleMessageListenerContainer replyContainer(ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(RPC_REPLY_QUEUE);
            container.setMessageListener(rabbitTemplate);
            return container;
        }
    }
}
