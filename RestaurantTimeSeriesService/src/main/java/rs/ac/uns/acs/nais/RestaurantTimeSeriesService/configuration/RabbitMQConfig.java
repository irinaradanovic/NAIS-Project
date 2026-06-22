package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "order.exchange";
    public static final String INFLUX_QUEUE = "restaurant.timeseries.queue";
    public static final String ROUTING_KEY = "restaurant.timeseries.log";
    public static final String RESTAURANT_RESPONSE_KEY = "restaurant.response.processed";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue influxQueue() {
        return new Queue(INFLUX_QUEUE, true);
    }

    @Bean
    public Binding bindingInflux(Queue influxQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(influxQueue).to(orderExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
