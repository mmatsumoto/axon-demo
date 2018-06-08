package br.com.zup.axon.bank.domain.account

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision
import java.io.Serializable

data class DepositMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId,
                               val transactionId: TransactionId,
                               val money: Money)
@Revision("2.0")
data class MoneyDepositedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant) // added r2.0

@Revision("1.0")
data class MoneyDepositRejectEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("money") val money: Money): Serializable
