package br.com.zup.axon.event.bank.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.AccountName
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.Tenant
import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision


@Revision("3.0")
data class AccountCreatedEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId,
        @JsonProperty("name") val name: AccountName,
        @JsonProperty("gender") val gender: Gender,    // added r3.0
        @JsonProperty("balance") val balance: Money,
        @JsonProperty("tenant") val tenant: Tenant)     // added r2.0

@Revision("1.0")
data class AccountNotFoundEvent @JsonCreator constructor(
        @JsonProperty("id") val id: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId)