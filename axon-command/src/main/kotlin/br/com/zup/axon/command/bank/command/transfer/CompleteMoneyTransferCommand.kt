package br.com.zup.axon.command.bank.command.transfer

import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CompleteMoneyTransferCommand @JsonCreator constructor(@field:TargetAggregateIdentifier
                                                                 @JsonProperty("transactionId") val transactionId: TransactionId)
