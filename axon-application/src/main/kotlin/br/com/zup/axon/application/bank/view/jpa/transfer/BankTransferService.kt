package br.com.zup.axon.application.bank.view.jpa.transfer

import br.com.zup.axon.application.bank.aggregate.BankTransferStatus
import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.event.bank.transfer.MoneyTransferCompletedEvent
import br.com.zup.axon.event.bank.transfer.MoneyTransferFailedEvent
import br.com.zup.axon.event.bank.transfer.TransferMoneyRequestedEvent
import org.springframework.stereotype.Service


interface BankTransferService {
    fun findAll(): List<BankTransferRepresentation>
    fun findOne(id: AccountId): BankTransferRepresentation?
    fun start(event: TransferMoneyRequestedEvent): BankTransferEntity
    fun fail(event: MoneyTransferFailedEvent): BankTransferEntity?
    fun complete(event: MoneyTransferCompletedEvent): BankTransferEntity?
}

@Service
class BankTransferServiceImpl(private val repository: BankTransferRepository) : BankTransferService {

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

    private fun toRepresentation(entity: BankTransferEntity) =
            BankTransferRepresentation(entity.transactionId,
                                       entity.sourceId,
                                       entity.destinationId,
                                       entity.amount,
                                       entity.status)

}
