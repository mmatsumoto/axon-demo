package br.com.zup.axon.command.bank.view.jpa.transfer

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.transfer.BankTransferStatus
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