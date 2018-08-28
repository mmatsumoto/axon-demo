package br.com.zup.axon.application.bank.aggregate

import br.com.zup.axon.application.bank.command.account.CloseAccountCommand
import br.com.zup.axon.application.bank.command.account.CreateAccountCommand
import br.com.zup.axon.application.bank.command.account.DepositMoneyCommand
import br.com.zup.axon.application.bank.command.account.WithdrawMoneyCommand
import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.TransactionId
import br.com.zup.axon.event.bank.account.AccountNotFoundEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateNotFoundException
import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage
import org.axonframework.messaging.MetaData
import org.slf4j.LoggerFactory

/**
 * @see AccountConfiguration
 */
open class AccountCommandHandler(private val repository: Repository<Account>,
                                 private val eventBus: EventBus) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @CommandHandler
    fun on(command: CreateAccountCommand, metaData: MetaData): AccountId =
            repository.newInstance { Account(command.id, command.name, command.gender, command.money, metaData) }
                    .identifier() as AccountId

    @CommandHandler
    fun on(command: DepositMoneyCommand, metaData: MetaData): TransactionId? = validateAggregate(command.accountId, command.transactionId) {
        repository.load(command.accountId)
                .invoke { it.deposit(command.accountId, command.transactionId, command.money, metaData) }
    }

    @CommandHandler
    fun on(command: WithdrawMoneyCommand): TransactionId? = validateAggregate(command.accountId, command.transactionId) {
        repository.load(command.accountId)
                .invoke { it.withdraw(command.accountId, command.transactionId, command.money) }
    }

    @CommandHandler
    fun on(command: CloseAccountCommand): Boolean =
        repository.load(command.id)
                .invoke(Account::close)

    private inline fun <R> validateAggregate(accountId: AccountId,
                                             transactionId: TransactionId,
                                             block: () -> R): R? =
            try {
                block()
            } catch (e: AggregateNotFoundException) {
                 AccountNotFoundEvent(accountId, transactionId)
                        .also { eventBus.publish(asEventMessage<AccountNotFoundEvent>(it)) }
                        .also { logger.error("AggregateNotFound $accountId. Publishing $it", it) }
                 null
            }

}
