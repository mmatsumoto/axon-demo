package br.com.zup.axon.kafka.consumer

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order


@EnableDiscoveryClient
@SpringBootApplication
class AxonKafkaConsumer {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    @Order(10000)
    fun log(c: EventProcessingConfiguration): CommandLineRunner {
        return CommandLineRunner {
            log.info("Trying to log processors:")
            c.eventProcessors()
                    .forEach { t, u ->
                        log.info("processorName $t, u: $u")


                        if (u is TrackingEventProcessor) {
                            log.info("$t: supportsReset: ${u.supportsReset()}")
                            log.info("$t: isRunning: ${u.isRunning}")
                            log.info("$t: processingStatus: ${u.processingStatus().values}")


                            log.info("Waiting 5s to restart the Tracking")
                            Thread.sleep(5000)

                            u.shutDown()
                            u.resetTokens()
                            u.start()
                        }

                    }

        }
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(AxonKafkaConsumer::class.java, *args)
}





