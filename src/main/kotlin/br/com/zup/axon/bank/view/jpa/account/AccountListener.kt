package br.com.zup.axon.bank.view.jpa.account

import br.com.zup.axon.bank.domain.account.AccountClosedEvent
import br.com.zup.axon.bank.domain.account.AccountCreatedEvent
import br.com.zup.axon.bank.domain.account.MoneyDepositedEvent
import br.com.zup.axon.bank.domain.account.MoneyRefundedEvent
import br.com.zup.axon.bank.domain.account.MoneyWithdrawnEvent
import org.axonframework.eventhandling.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class AccountListener(private val accountService: AccountService) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        accountService.create(event)
                .also { logger.info("$it saved at jpa view") }

    }

    @EventHandler
    fun on(event: MoneyDepositedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        accountService.deposit(event)
                .also { logger.info("$it deposited at jpa view") }

    }

    @EventHandler
    fun on(event: MoneyRefundedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        accountService.refund(event)
                .also { logger.info("$it refunded at jpa view") }

    }

    @EventHandler
    fun on(event: AccountClosedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        accountService.close(event)
                .also { logger.info("$it closed at jpa view") }

    }

    @EventHandler
    fun on(event: MoneyWithdrawnEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        accountService.withdraw(event)
                .also { logger.info("$it withdraw at jpa view") }

    }
}
