package rs.ac.uns.acs.nais.MockDeliveryService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String DELIVERY_REQUEST_QUEUE = "delivery.assignment.queue";
    public static final String DELIVERY_REQUEST_ROUTING_KEY = "delivery.assignment.requested";
    public static final String FINANCE_RESPONSE_QUEUE = "finance.saga.response.queue";
    public static final String DELIVERY_RESPONSE_ROUTING_KEY = "delivery.assignment.result";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue deliveryRequestQueue() {
        return new Queue(DELIVERY_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue financeResponseQueue() {
        return new Queue(FINANCE_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding deliveryRequestBinding(Queue deliveryRequestQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(deliveryRequestQueue).to(sagaExchange).with(DELIVERY_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding financeResponseBinding(Queue financeResponseQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(financeResponseQueue).to(sagaExchange).with(DELIVERY_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
