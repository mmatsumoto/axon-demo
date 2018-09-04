package br.com.zup.axon.command.bank.view.jpa.transfer

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.transfer.BankTransferStatus

data class BankTransferRepresentation(
        val transactionId: String,
        val sourceId: AccountId,
        val destinationId: AccountId,
        val amount: Money,
        val status: BankTransferStatus)