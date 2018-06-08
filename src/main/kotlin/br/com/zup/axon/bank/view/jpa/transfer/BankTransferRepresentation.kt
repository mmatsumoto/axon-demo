package br.com.zup.axon.bank.view.jpa.transfer

import br.com.zup.axon.bank.aggregate.BankTransferStatus
import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.Money

data class BankTransferRepresentation(
        val transactionId: String,
        val sourceId: AccountId,
        val destinationId: AccountId,
        val amount: Money,
        val status: BankTransferStatus)