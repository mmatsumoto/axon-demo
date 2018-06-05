package br.com.zup.axon.bank.aggregate

import br.com.zup.axon.bank.event.AccountCloseRejectEvent
import br.com.zup.axon.bank.event.AccountClosedEvent
import br.com.zup.axon.bank.event.AccountCreatedEvent
import br.com.zup.axon.bank.event.CloseAccountCommand
import br.com.zup.axon.bank.event.CreateAccountCommand
import br.com.zup.axon.bank.event.DepositMoneyCommand
import br.com.zup.axon.bank.event.MoneyDepositRejectEvent
import br.com.zup.axon.bank.event.MoneyDepositedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawRejectedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawnEvent
import br.com.zup.axon.bank.event.WithdrawMoneyCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
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

enum class AccountStatus {
    ACTIVE, CLOSED
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

    var status: AccountStatus = AccountStatus.ACTIVE
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
            if (this.isActive()) {
                MoneyDepositedEvent(c.accountId, c.transactionId, c.money, this.balance + c.money, metaData["tenant"] as String)
                        .apply { AggregateLifecycle.apply(this, metaData) }
                        .transactionId
            } else {
                MoneyDepositRejectEvent(c.accountId, c.transactionId, c.money)
                        .apply { AggregateLifecycle.apply(this, metaData) }
                        .transactionId
            }

    @CommandHandler
    fun on(c: WithdrawMoneyCommand, metaData: MetaData): TransactionId =
            if (canWithdraw(c.money)) {
                MoneyWithdrawnEvent(c.accountId, c.transactionId, c.money, this.balance - c.money)
                        .apply { AggregateLifecycle.apply(this, metaData) }
                        .transactionId
            } else {
                MoneyWithdrawRejectedEvent(c.accountId, c.transactionId, c.money, this.balance)
                        .apply { AggregateLifecycle.apply(this, metaData) }
                        .transactionId
            }

    private fun canWithdraw(money: Money) = this.isActive() && this.balance >= money

    @CommandHandler
    fun on(command: CloseAccountCommand): AccountId {
        if (this.balance != 0L) {
            apply(AccountCloseRejectEvent(command.id, this.balance))
        } else {
            apply(AccountClosedEvent(command.id))
        }
        return command.id
    }

    @EventSourcingHandler
    fun on(e: AccountCreatedEvent) {
//        log.info("Aggregate Account on: $e")
        this.id = e.id
        this.name = e.name
        this.balance = e.balance
        if (e.name.contains( " ")) {
            this.lastName = e.name.substringAfter(" ")
        }
        this.status = AccountStatus.ACTIVE
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
    fun on(@Suppress("UNUSED_PARAMETER") e: AccountClosedEvent) {
        this.status = AccountStatus.CLOSED
    }

    private fun isActive() = this.status == AccountStatus.ACTIVE


    // from snapshot (not necessary)
//    @EventHandler
//    fun on(account: Account) {
//        log.info("Aggregate reloaded from snapshot Account on: $account")
//        this.id = account.id
//        this.name = account.name
//        this.balance = account.balance
//        this.lastName = account.lastName
//        this.status = account.status
//    }

    override fun toString(): String {
        return "Account(id=$id, name=$name, balance=$balance)"
    }

    companion object {
        fun newId(): AccountId = UUID.randomUUID().toString()
    }
}