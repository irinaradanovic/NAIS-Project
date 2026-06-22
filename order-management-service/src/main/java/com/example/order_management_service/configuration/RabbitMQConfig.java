package com.example.order_management_service.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "order.exchange";
    // Ono što ovaj servis ČITA (Kreira queue i binding)
    public static final String ORDER_RESPONSE_QUEUE = "order.response.queue";
    public static final String RESTAURANT_RESPONSE_KEY = "restaurant.response.processed";

    // Ono gde ovaj servis ŠALJE (Samo konstante za slanje)
    public static final String RESTAURANT_ORDER_QUEUE_ROUTING_KEY = "order.created";
}
