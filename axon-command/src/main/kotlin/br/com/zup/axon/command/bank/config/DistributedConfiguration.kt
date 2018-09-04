package br.com.zup.axon.command.bank.config

import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Configuration

@EnableDiscoveryClient
@Configuration
class DistributedConfiguration {

    // the lines bellow are not required. axon auto configuration will do everything

//    @Bean
//    fun springCloudCommandRouter(discoveryClient: DiscoveryClient,
//                                 registration: Registration): SpringCloudCommandRouter {
//        return SpringCloudCommandRouter(discoveryClient, registration, AnnotationRoutingStrategy());
//    }
//
//    @Bean
//    fun springHttpCommandBusConnector(@Qualifier("localSegment") localSegment: CommandBus,
//                                      restTemplate: RestTemplate,
//                                      serializer: Serializer): CommandBusConnector {
//        return SpringHttpCommandBusConnector(localSegment, restTemplate, serializer);
//    }
//
//    @Primary
//    @Bean
//    fun springCloudDistributedCommandBus(commandRouter: CommandRouter, commandBusConnector: CommandBusConnector): DistributedCommandBus {
//        return DistributedCommandBus(commandRouter, commandBusConnector);
//    }
}