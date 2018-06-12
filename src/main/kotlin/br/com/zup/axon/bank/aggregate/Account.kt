package br.com.zup.axon.bank.aggregate

import br.com.zup.axon.bank.domain.account.AccountCloseRejectEvent
import br.com.zup.axon.bank.domain.account.AccountClosedEvent
import br.com.zup.axon.bank.domain.account.AccountCreatedEvent
import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.AccountName
import br.com.zup.axon.bank.domain.account.DepositMoneyCommand
import br.com.zup.axon.bank.domain.account.Money
import br.com.zup.axon.bank.domain.account.MoneyDepositRejectEvent
import br.com.zup.axon.bank.domain.account.MoneyDepositedEvent
import br.com.zup.axon.bank.domain.account.MoneyRefundRejectEvent
import br.com.zup.axon.bank.domain.account.MoneyRefundedEvent
import br.com.zup.axon.bank.domain.account.MoneyWithdrawRejectedEvent
import br.com.zup.axon.bank.domain.account.MoneyWithdrawnEvent
import br.com.zup.axon.bank.domain.account.RefundMoneyCommand
import br.com.zup.axon.bank.domain.account.TransactionId
import br.com.zup.axon.bank.domain.account.WithdrawMoneyCommand
import com.fasterxml.jackson.annotation.JsonIgnore
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Aggregate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


enum class Gender {
    MALE, FEMALE, UNKNOWN
}

enum class AccountStatus {
    ACTIVE, CLOSED
}

@Aggregate(repository = "customAccountRepository")
@Revision("3.0")
final class Account constructor() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @AggregateIdentifier
    lateinit var id: AccountId
        private set

    var name: AccountName? = null
        private set

    var balance: Money = 0L
        private set

    var lastName: String? = null
        private set

    var status: AccountStatus = AccountStatus.ACTIVE
        private set

//    @CommandHandler
//    constructor(command: CreateAccountCommand, metaData: MetaData) : this() {
//        apply(AccountCreatedEvent(command.id,
//                                  command.name,
//                                  command.gender,
//                                  command.money,
//                                  metaData["tenant"] as String // this tenant is being added by an Interceptor. check AxonConfiguration
//                                 ), metaData)
//    }

    constructor(id: AccountId, name: AccountName, gender: Gender, money: Money, metaData: MetaData) : this() {
        apply(AccountCreatedEvent(id, name, gender, money, metaData["tenant"] as String), metaData)
    }

    @CommandHandler
    fun on(cmd: DepositMoneyCommand, metaData: MetaData): TransactionId {
        when (this.isActive()) {
            true -> deposit(cmd, metaData)
            else -> apply(MoneyDepositRejectEvent(cmd.accountId, cmd.transactionId, cmd.money))
        }
        return cmd.transactionId
    }

    @CommandHandler
    fun on(cmd: WithdrawMoneyCommand): TransactionId {
        when (canWithdraw(cmd.money)) {
            true -> apply(MoneyWithdrawnEvent(cmd.accountId, cmd.transactionId, cmd.money, this.balance - cmd.money))
            else -> apply(MoneyWithdrawRejectedEvent(cmd.accountId, cmd.transactionId, cmd.money, this.balance))
        }
        return cmd.transactionId
    }

    @CommandHandler
    fun on(cmd: RefundMoneyCommand): TransactionId {
        when (this.isActive()) {
            true -> apply(MoneyRefundedEvent(cmd.accountId, cmd.transactionId, cmd.money, this.balance + cmd.money))
            else -> apply(MoneyRefundRejectEvent(cmd.accountId, cmd.transactionId, cmd.money))
        }
        return cmd.transactionId
    }

    fun close() {
        when (canClose()) {
            true -> apply(AccountClosedEvent(this.id))
            else -> apply(AccountCloseRejectEvent(this.id, this.balance))
                    .also { log.info("Account $id can`t be closed. balance $balance") }
        }
    }

    @EventSourcingHandler
    fun on(e: AccountCreatedEvent) {
        this.id = e.id
        this.name = e.name
        this.balance = e.balance
        if (e.name.contains(" ")) {
            this.lastName = e.name.substringAfter(" ")
        }
        this.status = AccountStatus.ACTIVE
    }

    @EventSourcingHandler
    fun on(e: MoneyDepositedEvent) {
        this.balance = e.balance
    }

    @EventSourcingHandler
    fun on(e: MoneyRefundedEvent) {
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

    @EventSourcingHandler
    fun on(@SuppressWarnings("UNUSED_PARAMETER") e: MoneyDepositRejectEvent) {
        log.info("Deposit reject: $e")
    }

    @EventSourcingHandler
    fun on(@SuppressWarnings("UNUSED_PARAMETER") e: MoneyWithdrawRejectedEvent) {
        log.info("Withdraw reject: $e")
    }

    //this is not necessary, but if you want to, this is how you apply the snapshot by yourself
//    @EventHandler
//    fun on(account: Account) {
//        log.info("Aggregate reloaded from snapshot Account on: $account")
//        this.id = account.id
//        this.name = account.name
//     // etc
//    }

    private fun deposit(cmd: DepositMoneyCommand, metaData: MetaData) {
        when (cmd.money == 666L) { // this magic number is used to simulate a command that was never succeed
            true -> throw IllegalArgumentException("666 is a baaaaad number!")
            else -> apply(MoneyDepositedEvent(cmd.accountId,
                                              cmd.transactionId,
                                              cmd.money,
                                              this.balance + cmd.money,
                                              metaData["tenant"] as String))
        }
    }

    @JsonIgnore
    fun canWithdraw(money: Money) = this.isActive() && this.balance >= money

    @JsonIgnore
    fun isActive() = this.status == AccountStatus.ACTIVE

    @JsonIgnore
    fun canClose() = this.balance == 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
    override fun toString(): String =
            "Account(id=$id, name=$name, balance=$balance, lastName=$lastName, status=$status)"

    companion object {
        fun newId(): AccountId = UUID.randomUUID().toString()
    }
}