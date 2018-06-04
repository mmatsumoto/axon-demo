package br.com.zup.axon.saga.aggregate

import br.com.zup.axon.saga.event.AccountCreatedEvent
import br.com.zup.axon.saga.event.CreateAccountCommand
import br.com.zup.axon.saga.event.DepositMoneyCommand
import br.com.zup.axon.saga.event.MoneyDepositedEvent
import br.com.zup.axon.saga.event.MoneyWithdrawRejectedEvent
import br.com.zup.axon.saga.event.MoneyWithdrawnEvent
import br.com.zup.axon.saga.event.WithdrawMoneyCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Aggregate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

typealias AccountId = String
typealias TransactionId = String
typealias AccountName = String
typealias Money = Long
typealias Tenant = String

enum class Gender {
    MALE, FEMALE, UNKNOWN
}

@Aggregate(snapshotTriggerDefinition = "eventCountSnapshot")
@Revision("3.0")
final class Account constructor() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)


    @AggregateIdentifier
    var id: AccountId? = null
        private set

    var name: AccountName? = null
        private set

    var balance: Money = 0L
        private set

    var lastName: String? = null
        private set

    @CommandHandler
    constructor(command: CreateAccountCommand, metaData: MetaData) : this() {
        apply(AccountCreatedEvent(command.id,
                                  command.name,
                                  command.gender,
                                  command.money,
                                  metaData["tenant"] as String
                                 ), metaData)
    }

    @CommandHandler
    fun on(c: DepositMoneyCommand, metaData: MetaData): TransactionId =
            MoneyDepositedEvent(c.accountId, c.transactionId, c.money, this.balance + c.money, metaData["tenant"] as String)
                    .apply { AggregateLifecycle.apply(this, metaData) }
                    .transactionId

    @CommandHandler
    fun on(c: WithdrawMoneyCommand, metaData: MetaData): TransactionId =
            if (this.balance < c.money) {
                MoneyWithdrawRejectedEvent(c.accountId, c.transactionId, c.money, this.balance)
                        .apply { AggregateLifecycle.apply(this, metaData) }
                        .transactionId
            } else {
                MoneyWithdrawnEvent(c.accountId, c.transactionId, c.money, this.balance - c.money)
                        .apply { AggregateLifecycle.apply(this, metaData) }
                        .transactionId
            }

    @EventSourcingHandler
    fun on(e: AccountCreatedEvent) {
        log.info("Aggregate Account on: $e")
        this.id = e.id
        this.name = e.name
        this.balance = e.balance
        if (e.name.contains( " ")) {
            this.lastName = e.name.substringAfter(" ")
        }
    }

    @EventSourcingHandler
    fun on(e: MoneyDepositedEvent) {
        this.balance = e.balance
    }

    @EventSourcingHandler
    fun on(e: MoneyWithdrawnEvent) {
        this.balance = e.balance
    }

    @EventSourcingHandler
    fun on(e: MoneyWithdrawRejectedEvent) {
    }

    @EventHandler
    fun on(account: Account) {
        log.info("Aggregate reloaded from snapshot Account on: $account")
        this.id = account.id
        this.name = account.name
        this.balance = account.balance
        this.lastName = account.lastName
    }

    override fun toString(): String {
        return "Account(id=$id, name=$name, balance=$balance)"
    }

    companion object {
        fun newId(): AccountId = UUID.randomUUID().toString()
    }
}