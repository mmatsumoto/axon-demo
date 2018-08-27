package br.com.zup.axon.kafka.consumer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class AxonKafkaConsumer


fun main(args: Array<String>) {
    SpringApplication.run(AxonKafkaConsumer::class.java, *args)
}



