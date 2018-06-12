package br.com.zup.axon.bank.domain.account

import br.com.zup.axon.bank.aggregate.Gender
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision

data class CreateAccountCommand(@field:TargetAggregateIdentifier
                                val id: AccountId,
                                val name: AccountName,
                                val gender: Gender,
                                val money: Money)

@Revision("3.0")
data class AccountCreatedEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId,
        @JsonProperty("name") val name: AccountName,
        @JsonProperty("gender") val gender: Gender,    // added r3.0
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant)     // added r2.0

@Revision("1.0")
data class AccountNotFoundEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId)