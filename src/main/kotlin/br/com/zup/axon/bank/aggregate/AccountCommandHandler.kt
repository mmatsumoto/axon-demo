package br.com.zup.axon.bank.aggregate

import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.AccountNotFoundEvent
import br.com.zup.axon.bank.domain.account.CloseAccountCommand
import br.com.zup.axon.bank.domain.account.CreateAccountCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateNotFoundException
import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage
import org.axonframework.messaging.MetaData
import org.slf4j.LoggerFactory


open class AccountCommandHandler(private val repository: Repository<Account>,
                                 private val eventBus: EventBus) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @CommandHandler
    fun on(command: CreateAccountCommand, metaData: MetaData): AccountId {
        return repository
                .newInstance { Account(command.id, command.name, command.gender, command.money, metaData) }
                .identifier() as AccountId
    }

    @CommandHandler
    fun on(command: CloseAccountCommand) {
        try {
            repository.load(command.id)
                    .execute(Account::close)
        } catch (e: AggregateNotFoundException) {
            logger.info("CloseAccountCommand failed. AggregateNotFound ${command.id}")
            eventBus.publish(asEventMessage<AccountNotFoundEvent>(AccountNotFoundEvent(command.id)))
        }
    }

}
