package br.com.zup.axon.saga.view.jpa

import br.com.zup.axon.saga.aggregate.AccountId
import br.com.zup.axon.saga.aggregate.AccountName
import br.com.zup.axon.saga.aggregate.Money
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "account")
data class AccountEntity(
        @Id
        val id: AccountId,

        @Column
        val name: AccountName,

        @Column
        val balance: Money)