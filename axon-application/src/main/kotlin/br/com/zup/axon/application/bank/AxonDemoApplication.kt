package br.com.zup.axon.application.bank

import br.com.zup.axon.application.bank.view.memory.AccountMemoryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class AxonDemoApplication {

    private val log: Logger = LoggerFactory.getLogger(AxonDemoApplication::class.java)

    @Bean
    fun resetTokenAccountMemoryProcessor(accountMemoryService: AccountMemoryService): CommandLineRunner {
        return CommandLineRunner {
            log.info("\nReplaying AccountMemoryListener: ")
            accountMemoryService.replay()
        }
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(AxonDemoApplication::class.java, *args)
}



