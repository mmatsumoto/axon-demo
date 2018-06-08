package br.com.zup.axon.bank.domain.account

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision


data class WithdrawMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId,
                                val transactionId: TransactionId,
                                val money: Money)

@Revision("1.0")
data class MoneyWithdrawnEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)

@Revision("1.0")
data class MoneyWithdrawRejectedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)
