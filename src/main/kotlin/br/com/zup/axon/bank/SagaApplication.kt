package br.com.zup.axon.bank

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SagaApplication

fun main(args: Array<String>) {
    SpringApplication.run(SagaApplication::class.java, *args)
}
