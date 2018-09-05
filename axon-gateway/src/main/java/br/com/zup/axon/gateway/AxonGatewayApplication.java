package br.com.zup.axon.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class AxonGatewayApplication {

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // check the application.yml to see another way to route services

                .route("axon-command-app", p ->
                        p.path("/**").uri("lb://axon-command"))

                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(AxonGatewayApplication.class, args);
    }
}
