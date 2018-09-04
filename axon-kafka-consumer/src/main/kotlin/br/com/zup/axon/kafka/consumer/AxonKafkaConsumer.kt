package br.com.zup.axon.kafka.consumer

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean


@EnableDiscoveryClient
@SpringBootApplication
class AxonKafkaConsumer

fun main(args: Array<String>) {
    SpringApplication.run(AxonKafkaConsumer::class.java, *args)
}





