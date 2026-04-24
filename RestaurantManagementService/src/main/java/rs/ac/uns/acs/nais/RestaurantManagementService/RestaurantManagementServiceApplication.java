package rs.ac.uns.acs.nais.RestaurantManagementService;

import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class RestaurantManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantManagementServiceApplication.class, args);
	}


}
