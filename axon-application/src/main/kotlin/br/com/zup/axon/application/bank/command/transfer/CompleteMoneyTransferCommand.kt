package br.com.zup.axon.application.bank.command.transfer

import br.com.zup.axon.application.bank.domain.account.TransactionId
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CompleteMoneyTransferCommand(@field:TargetAggregateIdentifier val transactionId: TransactionId)
