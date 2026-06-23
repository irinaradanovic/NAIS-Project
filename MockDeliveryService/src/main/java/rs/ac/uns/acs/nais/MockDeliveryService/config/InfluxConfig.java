package rs.ac.uns.acs.nais.MockDeliveryService.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxConfig {

    @Bean
    public InfluxDBClient influxDBClient(@Value("${spring.influx.url}") String url,
                                         @Value("${spring.influx.token}") String token,
                                         @Value("${spring.influx.org}") String org,
                                         @Value("${spring.influx.bucket}") String bucket) {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
