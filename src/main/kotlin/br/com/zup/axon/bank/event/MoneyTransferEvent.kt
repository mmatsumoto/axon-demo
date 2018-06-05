package br.com.zup.axon.bank.event

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.Money
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision


data class RequestTransferMoneyCommand(val transactionId: String, val sourceId: AccountId, val destinationId: AccountId, val amount: Money)
@Revision("1.0")
data class TransferMoneyRequestedEvent @JsonCreator constructor(
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("sourceId") val sourceId: AccountId,
        @JsonProperty("destinationId") val destinationId: AccountId,
        @JsonProperty("amount") val amount: Money)



data class CompleteMoneyTransferCommand(@field:TargetAggregateIdentifier val transactionId: String)
@Revision("1.0")
data class MoneyTransferCompletedEvent @JsonCreator constructor(
        @JsonProperty("transactionId") val transactionId: String)


data class FailMoneyTransferCommand(@field:TargetAggregateIdentifier val transactionId: String)
@Revision("1.0")
data class MoneyTransferFailedEvent @JsonCreator constructor(
        @JsonProperty("transactionId") val transactionId: String)