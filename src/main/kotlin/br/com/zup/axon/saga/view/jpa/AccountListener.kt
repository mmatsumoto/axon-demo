package br.com.zup.axon.saga.view.jpa

import br.com.zup.axon.saga.event.AccountCreatedEvent
import br.com.zup.axon.saga.event.MoneyDepositedEvent
import br.com.zup.axon.saga.event.MoneyWithdrawnEvent
import org.axonframework.eventhandling.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class AccountListener(private val accountService: AccountService) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        logger.info("event received at ${this.javaClass.name}")

        accountService.create(event)
                .also { logger.info("$it saved at jpa view") }

    }

    @EventHandler
    fun on(event: MoneyDepositedEvent) {
        logger.info("event received at ${this.javaClass.name}")

        accountService.deposit(event)
                .also { logger.info("$it deposited at jpa view") }

    }

    @EventHandler
    fun on(event: MoneyWithdrawnEvent) {
        logger.info("event received at ${this.javaClass.name}")

        accountService.withdraw(event)
                .also { logger.info("$it withdraw at jpa view") }

    }
}
