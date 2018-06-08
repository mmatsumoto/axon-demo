package br.com.zup.axon.bank.view.jpa.transfer

import br.com.zup.axon.bank.aggregate.BankTransferStatus
import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.transfer.MoneyTransferCompletedEvent
import br.com.zup.axon.bank.domain.transfer.MoneyTransferFailedEvent
import br.com.zup.axon.bank.domain.transfer.TransferMoneyRequestedEvent
import org.axonframework.config.EventHandlingConfiguration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


interface BankTransferService {
    fun findAll(): List<BankTransferRepresentation>
    fun findOne(id: AccountId): BankTransferRepresentation?

    fun deleteToken()
    fun registerTrackingProcessor()
    fun start(event: TransferMoneyRequestedEvent): BankTransferEntity
    fun fail(event: MoneyTransferFailedEvent): BankTransferEntity?
    fun complete(event: MoneyTransferCompletedEvent): BankTransferEntity?
}

@Service
class BankTransferServiceImpl(private val repository: BankTransferRepository,
                              private val config: EventHandlingConfiguration,
                              private val jdbcTemplate: JdbcTemplate) : BankTransferService {

    override fun start(event: TransferMoneyRequestedEvent): BankTransferEntity =
            repository.save(BankTransferEntity(event.transactionId,
                                               event.sourceId,
                                               event.destinationId,
                                               event.amount,
                                               BankTransferStatus.STARTED))

    override fun fail(event: MoneyTransferFailedEvent): BankTransferEntity? =
            repository.findOne(event.transactionId)
                    ?.copy(status = BankTransferStatus.FAILED)
                    .let(repository::save)

    override fun complete(event: MoneyTransferCompletedEvent): BankTransferEntity? =
            repository.findOne(event.transactionId)
                    ?.copy(status = BankTransferStatus.COMPLETED)
                    .let(repository::save)

    override fun findAll(): List<BankTransferRepresentation> =
            repository.findAll()
                    .map(::toRepresentation)

    override fun findOne(id: AccountId): BankTransferRepresentation? =
            repository.findOne(id)
                    ?.let(::toRepresentation)

    override fun registerTrackingProcessor() {
        config.registerTrackingProcessor(BankTransferListener.GROUP_NAME)
    }

    override fun deleteToken() {
        jdbcTemplate.update("DELETE FROM token_entry where processor_name = ? ",
                            BankTransferListener.GROUP_NAME)
    }

    private fun toRepresentation(entity: BankTransferEntity) =
            BankTransferRepresentation(entity.transactionId,
                                       entity.sourceId,
                                       entity.destinationId,
                                       entity.amount,
                                       entity.status)

}
