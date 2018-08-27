package br.com.zup.axon.kafka.consumer.listener

import br.com.zup.axon.event.bank.account.AccountCreatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("AccountKafkaProcessor")
class AccountListener {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        logger.info("AccountKafkaProcessor: $event received at ${this.javaClass.name}")

    }

}