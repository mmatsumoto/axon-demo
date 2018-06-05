package br.com.zup.axon.bank.view.jpa

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.BankTransferStatus
import br.com.zup.axon.bank.aggregate.Money

data class BankTransferRepresentation(
        val transactionId: String,
        val sourceId: AccountId,
        val destinationId: AccountId,
        val amount: Money,
        val status: BankTransferStatus)