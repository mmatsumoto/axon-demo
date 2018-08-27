package br.com.zup.axon.event.bank.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision
import java.io.Serializable

@Revision("1.0")
data class MoneyRefundedEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("money") val money: Money,
        @JsonProperty("balance") val balance: Money)

// it's necessary implement Serializable if you are going to schedule this event with Quartz
@Revision("1.0")
data class MoneyRefundRejectEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("money") val money: Money): Serializable

