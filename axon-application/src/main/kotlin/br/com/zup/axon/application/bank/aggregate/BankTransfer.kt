package br.com.zup.axon.application.bank.aggregate

import br.com.zup.axon.application.bank.command.transfer.CompleteMoneyTransferCommand
import br.com.zup.axon.application.bank.command.transfer.FailMoneyTransferCommand
import br.com.zup.axon.application.bank.command.transfer.RequestTransferMoneyCommand
import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.transfer.BankTransferStatus
import br.com.zup.axon.event.bank.transfer.MoneyTransferCompletedEvent
import br.com.zup.axon.event.bank.transfer.MoneyTransferFailedEvent
import br.com.zup.axon.event.bank.transfer.TransferMoneyRequestedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
@Revision("1.0")
final class BankTransfer constructor() {

    @AggregateIdentifier
    var transactionId: String? = null
        private set

    var sourceId: AccountId? = null
        private set

    var destinationId: AccountId? = null
        private set

    var amount: Money? = null
        private set

    var status: BankTransferStatus? = null
        private set

    @CommandHandler
    constructor(command: RequestTransferMoneyCommand) : this() {
        apply(TransferMoneyRequestedEvent(command.transactionId,
                                          command.sourceId,
                                          command.destinationId,
                                          command.amount))
    }

    @CommandHandler
    fun on(command: CompleteMoneyTransferCommand) {
        apply(MoneyTransferCompletedEvent(command.transactionId))
    }

    @CommandHandler
    fun on(command: FailMoneyTransferCommand) {
        apply(MoneyTransferFailedEvent(command.transactionId))
    }

    @EventSourcingHandler
    fun on(event: TransferMoneyRequestedEvent) {
        this.transactionId = event.transactionId
        this.sourceId = event.sourceId
        this.destinationId = event.destinationId
        this.amount = event.amount
        this.status = BankTransferStatus.STARTED
    }

    @EventSourcingHandler
    fun on(@Suppress("UNUSED_PARAMETER") event: MoneyTransferFailedEvent) {
        this.status = BankTransferStatus.FAILED
    }

    @EventSourcingHandler
    fun on(@Suppress("UNUSED_PARAMETER") event: MoneyTransferCompletedEvent) {
        this.status = BankTransferStatus.COMPLETED
    }

}