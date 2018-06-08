package br.com.zup.axon.bank.domain.transfer

import br.com.zup.axon.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision

data class CompleteMoneyTransferCommand(@field:TargetAggregateIdentifier val transactionId: TransactionId)

@Revision("1.0")
data class MoneyTransferCompletedEvent @JsonCreator constructor(
        @JsonProperty("transactionId") val transactionId: TransactionId)
