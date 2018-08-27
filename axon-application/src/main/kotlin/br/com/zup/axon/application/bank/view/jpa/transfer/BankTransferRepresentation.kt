package br.com.zup.axon.application.bank.view.jpa.transfer

import br.com.zup.axon.application.bank.aggregate.BankTransferStatus
import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money

data class BankTransferRepresentation(
        val transactionId: String,
        val sourceId: AccountId,
        val destinationId: AccountId,
        val amount: Money,
        val status: BankTransferStatus)