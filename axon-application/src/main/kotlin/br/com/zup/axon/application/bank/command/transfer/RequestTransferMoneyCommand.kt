package br.com.zup.axon.application.bank.command.transfer

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId

data class RequestTransferMoneyCommand(val transactionId: TransactionId,
                                       val sourceId: AccountId,
                                       val destinationId: AccountId,
                                       val amount: Money)

