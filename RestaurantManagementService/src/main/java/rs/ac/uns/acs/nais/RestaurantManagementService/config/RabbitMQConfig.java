package rs.ac.uns.acs.nais.RestaurantManagementService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "order.exchange";
    public static final String RESTAURANT_RESPONSE_KEY = "restaurant.response.processed";
    public static final String RESTAURANT_ORDER_QUEUE = "restaurant.order.queue";
    public static final String RESTAURANT_ORDER_ROUTING_KEY = "order.created";
    public static final String TIMESERIES_ROUTING_KEY = "restaurant.timeseries.log";
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }
    @Bean
    public Queue restaurantOrderQueue() {
        return new Queue(RESTAURANT_ORDER_QUEUE, true);
    }

    @Bean
    public Binding bindingRestaurantOrder(Queue restaurantOrderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(restaurantOrderQueue).to(orderExchange).with(RESTAURANT_ORDER_ROUTING_KEY);
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
