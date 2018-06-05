package br.com.zup.axon.bank.view.jpa

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.Money
import br.com.zup.axon.bank.event.AccountCreatedEvent
import br.com.zup.axon.bank.event.MoneyDepositedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawnEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


interface AccountService {
    fun findAll(): List<AccountRepresentation>
    fun findOne(id: AccountId): AccountRepresentation?
    fun create(event: AccountCreatedEvent): AccountEntity
    fun deposit(event: MoneyDepositedEvent): AccountEntity
    fun withdraw(event: MoneyWithdrawnEvent): AccountEntity
}

@Service
class AccountServiceImpl(private val repository: AccountRepository) : AccountService {

    override fun findAll(): List<AccountRepresentation> =
            repository.findAll()
                    .map(this::toRepresentation)

    override fun findOne(id: AccountId): AccountRepresentation? =
            repository.findOne(id)?.let(this::toRepresentation)

    @Transactional(propagation = Propagation.REQUIRED)
    override fun create(event: AccountCreatedEvent): AccountEntity =
            repository.save(AccountEntity(event.id,
                                          event.name,
                                          event.balance))

    @Transactional(propagation = Propagation.REQUIRED)
    override fun deposit(event: MoneyDepositedEvent): AccountEntity =
            updateBalance(event.accountId, event.balance)

    @Transactional(propagation = Propagation.REQUIRED)
    override fun withdraw(event: MoneyWithdrawnEvent): AccountEntity =
            updateBalance(event.accountId, event.balance)

    private fun updateBalance(accountId: AccountId, balance: Money ): AccountEntity {
        return repository.findOne(accountId)
                .copy(balance = balance)
                .let(repository::save)
    }

    private fun toRepresentation(entity: AccountEntity) =
            AccountRepresentation(entity.id, entity.name, entity.balance)

}
