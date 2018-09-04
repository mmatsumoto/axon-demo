package br.com.zup.axon.command.bank

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class AxonCommandApplication {

    private val log: Logger = LoggerFactory.getLogger(AxonCommandApplication::class.java)

//    @Bean
//    fun resetTokenAccountMemoryProcessor(accountMemoryService: AccountMemoryService): CommandLineRunner {
//        return CommandLineRunner {
//            log.info("\nReplaying AccountMemoryListener: ")
//            accountMemoryService.replay()
//        }
//    }
}


fun main(args: Array<String>) {
    SpringApplication.run(AxonCommandApplication::class.java, *args)
}



