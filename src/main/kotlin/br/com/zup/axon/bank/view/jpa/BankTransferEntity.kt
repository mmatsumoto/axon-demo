package br.com.zup.axon.bank.view.jpa

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.BankTransferStatus
import br.com.zup.axon.bank.aggregate.Money
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "bank_transfer")
data class BankTransferEntity(
        @Id
        val transactionId: String,

        @Column
        val sourceId: AccountId,

        @Column
        val destinationId: AccountId,

        @Column
        val amount: Money,

        @Column
        @Enumerated(EnumType.STRING)
        val status: BankTransferStatus)