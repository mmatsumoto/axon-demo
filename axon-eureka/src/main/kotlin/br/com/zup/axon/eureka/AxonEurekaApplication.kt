package br.com.zup.axon.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer


@EnableEurekaServer
@SpringBootApplication
class AxonEurekaApplication

fun main(args: Array<String>) {
    SpringApplication.run(AxonEurekaApplication::class.java, *args)
}
