package br.com.zup.axon.application.bank.view.jpa.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.account.AccountClosedEvent
import br.com.zup.axon.event.bank.account.AccountCreatedEvent
import br.com.zup.axon.event.bank.account.AccountStatus
import br.com.zup.axon.event.bank.account.MoneyDepositedEvent
import br.com.zup.axon.event.bank.account.MoneyRefundedEvent
import br.com.zup.axon.event.bank.account.MoneyWithdrawnEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


interface AccountService {
    fun findAll(): List<AccountRepresentation>
    fun findOne(id: AccountId): AccountRepresentation?
    fun create(event: AccountCreatedEvent): AccountEntity
    fun deposit(event: MoneyDepositedEvent): AccountEntity
    fun refund(event: MoneyRefundedEvent): AccountEntity
    fun withdraw(event: MoneyWithdrawnEvent): AccountEntity
    fun close(event: AccountClosedEvent): AccountEntity
}

@Service
class AccountServiceImpl(private val repository: AccountRepository) : AccountService {

    override fun findAll(): List<AccountRepresentation> =
            repository.findAll()
                    .map(::toRepresentation)

    override fun findOne(id: AccountId): AccountRepresentation? =
            repository.findOne(id)?.let(::toRepresentation)

    @Transactional(propagation = Propagation.REQUIRED)
    override fun create(event: AccountCreatedEvent): AccountEntity =
            repository.save(AccountEntity(event.id,
                                          event.name,
                                          event.gender,
                                          event.balance,
                                          AccountStatus.ACTIVE))

    @Transactional(propagation = Propagation.REQUIRED)
    override fun close(event: AccountClosedEvent): AccountEntity =
        repository.findOne(event.accountId)
                .copy(status = AccountStatus.CLOSED)
                .let(repository::save)

    @Transactional(propagation = Propagation.REQUIRED)
    override fun deposit(event: MoneyDepositedEvent): AccountEntity =
            updateBalance(event.accountId, event.balance)

    @Transactional(propagation = Propagation.REQUIRED)
    override fun refund(event: MoneyRefundedEvent): AccountEntity =
            updateBalance(event.accountId, event.balance)

    @Transactional(propagation = Propagation.REQUIRED)
    override fun withdraw(event: MoneyWithdrawnEvent): AccountEntity =
            updateBalance(event.accountId, event.balance)

    private fun updateBalance(accountId: AccountId, balance: Money): AccountEntity =
            repository.findOne(accountId)
                    .copy(balance = balance)
                    .let(repository::save)

    private fun toRepresentation(entity: AccountEntity) =
            AccountRepresentation(entity.id, entity.name, entity.gender, entity.balance, entity.status)

}
