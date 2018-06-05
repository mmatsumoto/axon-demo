package br.com.zup.axon.bank.view.jpa

import br.com.zup.axon.bank.event.MoneyTransferCompletedEvent
import br.com.zup.axon.bank.event.MoneyTransferFailedEvent
import br.com.zup.axon.bank.event.TransferMoneyRequestedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
@ProcessingGroup(BankTransferListener.GROUP_NAME)
class BankTransferListener(private val bankTransferService: BankTransferService) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @EventHandler
    fun on(event: TransferMoneyRequestedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        bankTransferService.start(event)
                .also { logger.info("$it saved at jpa view") }
    }

    @EventHandler
    fun on(event: MoneyTransferFailedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        bankTransferService.fail(event)
                .also { logger.info("$it saved at jpa view") }
    }

    @EventHandler
    fun on(event: MoneyTransferCompletedEvent) {
        logger.info("$event received at ${this.javaClass.name}")

        bankTransferService.complete(event)
                .also { logger.info("$it saved at jpa view") }
    }

    companion object {
        const val GROUP_NAME = "BankTransferGroup"
    }
}
