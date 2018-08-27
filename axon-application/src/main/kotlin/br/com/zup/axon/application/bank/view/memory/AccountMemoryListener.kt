package br.com.zup.axon.application.bank.view.memory

import br.com.zup.axon.application.bank.view.memory.AccountMemoryListener.Companion.GROUP_NAME
import br.com.zup.axon.event.bank.account.AccountClosedEvent
import br.com.zup.axon.event.bank.account.AccountCreatedEvent
import br.com.zup.axon.event.bank.account.MoneyDepositedEvent
import br.com.zup.axon.event.bank.account.MoneyRefundedEvent
import br.com.zup.axon.event.bank.account.MoneyWithdrawnEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
@ProcessingGroup(GROUP_NAME)
class AccountMemoryListener(private val accountMemoryService: AccountMemoryService) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        log.info("AccountMemoryListener e: $event")

        accountMemoryService.insertEvent(event.id, event)
    }

    @EventHandler
    fun on(event: MoneyDepositedEvent) {
        log.info("AccountMemoryListener e: $event")

        accountMemoryService.insertEvent(event.accountId, event)
    }

    @EventHandler
    fun on(event: MoneyWithdrawnEvent) {
        log.info("AccountMemoryListener e: $event")

        accountMemoryService.insertEvent(event.accountId, event)
    }

    @EventHandler
    fun on(event: MoneyRefundedEvent) {
        log.info("AccountMemoryListener e: $event")

        accountMemoryService.insertEvent(event.accountId, event)
    }

    @EventHandler
    fun on(event: AccountClosedEvent) {
        log.info("AccountMemoryListener e: $event")

        accountMemoryService.insertEvent(event.accountId, event)
    }

    companion object {
        const val GROUP_NAME = "AccountMemoryGroup"
    }

}