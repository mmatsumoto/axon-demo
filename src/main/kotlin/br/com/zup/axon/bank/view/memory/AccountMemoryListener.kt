package br.com.zup.axon.bank.view.memory

import br.com.zup.axon.bank.event.AccountCreatedEvent
import br.com.zup.axon.bank.event.MoneyDepositedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawnEvent
import br.com.zup.axon.bank.view.memory.AccountMemoryListener.Companion.GROUP_NAME
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
        log.info("on AccountMemoryListener e: $event")

        accountMemoryService.insertEvent(event.id, event)
    }

    @EventHandler
    fun on(event: MoneyDepositedEvent) {
        log.info("on MoneyDepositedEvent e: $event")

        accountMemoryService.insertEvent(event.accountId, event)
    }

    @EventHandler
    fun on(event: MoneyWithdrawnEvent) {
        log.info("on MoneyWithdrawnEvent e: $event")

        accountMemoryService.insertEvent(event.accountId, event)
    }

    companion object {
        const val GROUP_NAME = "AccountMemoryGroup"
    }

}