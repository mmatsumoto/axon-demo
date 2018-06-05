package br.com.zup.axon.bank.aggregate

import br.com.zup.axon.bank.event.CompleteMoneyTransferCommand
import br.com.zup.axon.bank.event.FailMoneyTransferCommand
import br.com.zup.axon.bank.event.MoneyTransferCompletedEvent
import br.com.zup.axon.bank.event.MoneyTransferFailedEvent
import br.com.zup.axon.bank.event.TransferMoneyRequestedEvent
import br.com.zup.axon.bank.event.RequestTransferMoneyCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler

enum class BankTransferStatus {
    STARTED, FAILED, COMPLETED
}

@Aggregate
final class BankTransfer constructor(){

    @AggregateIdentifier
    var transactionId: String? = null
        private set

    var sourceId: AccountId? = null
        private set

    var destinationId: AccountId? = null
        private set

    var value: Money? = null
        private set

    var status: BankTransferStatus? = null
        private set

    @CommandHandler
    constructor(command: RequestTransferMoneyCommand): this() {
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
        this.value = event.amount
        this.status = BankTransferStatus.STARTED
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferFailedEvent) {
        this.status = BankTransferStatus.FAILED
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferCompletedEvent) {
        this.status = BankTransferStatus.COMPLETED
    }

}