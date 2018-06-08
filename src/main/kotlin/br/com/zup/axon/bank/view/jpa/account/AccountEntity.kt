package br.com.zup.axon.bank.view.jpa.account

import br.com.zup.axon.bank.aggregate.AccountStatus
import br.com.zup.axon.bank.aggregate.Gender
import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.AccountName
import br.com.zup.axon.bank.domain.account.Money
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
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
        @Enumerated(EnumType.STRING)
        val gender: Gender,

        @Column
        val balance: Money,

        @Column
        @Enumerated(EnumType.STRING)
        val status: AccountStatus)