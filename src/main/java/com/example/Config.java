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
    public static final String RPC_REQUEST_KEY = "rpc.request.queue";
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
    public static Queue replyQueue() {
        return new Queue(RPC_REPLY_QUEUE);
    }

    @Bean
//    @Profile("client")
    public Binding requestBinding(Queue requestQueue, TopicExchange rpcExchange) {
        return BindingBuilder.bind(requestQueue).to(rpcExchange).with(RPC_REQUEST_KEY);
    }

    @Bean
    public Binding responseBinding(TopicExchange rpcExchange, Queue replyQueue) {
        return BindingBuilder.bind(replyQueue).to(rpcExchange).with(RPC_REPLY_QUEUE);
    }

    @Profile("client")
    private static class ServerConfig {
        @Bean
        RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setReplyAddress(replyQueue().getName());
//            template.setReplyTimeout(10000);
            template.setMessageConverter(new SimpleMessageConverter()); //Change 1 for NumberFormatException
            return template;
        }

        @Bean
        SimpleMessageListenerContainer replyContainer(ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueues(replyQueue());
            container.setMessageListener(rabbitTemplate);
            return container;
        }

//        @Bean
//        public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
////            rabbitTemplate.setMessageConverter(msgConv());
//            rabbitTemplate.setMessageConverter(new SimpleMessageConverter()); //Change 1 for NumberFormatException
//            rabbitTemplate.setReplyAddress(replyQueue().getName());
//            rabbitTemplate.setReplyTimeout(60000);
//            rabbitTemplate.setUseDirectReplyToContainer(false);
//            return rabbitTemplate;
//        }
//
//        @Bean
//        public SimpleMessageListenerContainer replyListenerContainer(ConnectionFactory connectionFactory) {
//            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//            container.setConnectionFactory(connectionFactory);
//            container.setQueues(replyQueue());
//            container.setMessageListener(amqpTemplate(connectionFactory));
//            return container;
//        }

//        @Bean
//        public Queue replyQueue() {
//            return new Queue(RPC_REPLY_QUEUE);
//        }

    }
}
