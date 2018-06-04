package br.com.zup.axon.saga.event

import br.com.zup.axon.saga.aggregate.AccountId
import br.com.zup.axon.saga.aggregate.AccountName
import br.com.zup.axon.saga.aggregate.Gender
import br.com.zup.axon.saga.aggregate.Money
import br.com.zup.axon.saga.aggregate.Tenant
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision

data class CreateAccountCommand(@field:TargetAggregateIdentifier val id: AccountId, val name: AccountName, val gender: Gender, val money: Money)
@Revision("3.0")
data class AccountCreatedEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId,
        @JsonProperty("name") val name: AccountName,
        @JsonProperty("gender") val gender: Gender,    // added v 3.0
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant     // added v 2.0
        )


data class DepositMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId,
                               val transactionId: String,
                               val money: Money)

@Revision("2.0")
data class MoneyDepositedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant)

data class WithdrawMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId, val transactionId: String, val money: Money)
@Revision("1.0")
data class MoneyWithdrawnEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)

@Revision("1.0")
data class MoneyWithdrawRejectedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: String,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)


@Revision("1.0")
data class AccountSnapshotEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId,
        @JsonProperty("name") val name: AccountName,
        @JsonProperty("gender") val gender: Gender)