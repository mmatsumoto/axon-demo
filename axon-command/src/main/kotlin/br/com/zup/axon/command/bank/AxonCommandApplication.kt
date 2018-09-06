package br.com.zup.axon.command.bank

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class AxonCommandApplication


fun main(args: Array<String>) {
    SpringApplication.run(AxonCommandApplication::class.java, *args)
}



