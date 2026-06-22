package com.example.order_management_service.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "order.exchange";
    public static final String ORDER_RESPONSE_QUEUE = "order.response.queue";
    public static final String RESTAURANT_RESPONSE_KEY = "restaurant.response.processed";
    public static final String RESTAURANT_ORDER_QUEUE_ROUTING_KEY = "order.created";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue orderResponseQueue() {
        return new Queue(ORDER_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding orderResponseBinding(Queue orderResponseQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderResponseQueue).to(orderExchange).with(RESTAURANT_RESPONSE_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
