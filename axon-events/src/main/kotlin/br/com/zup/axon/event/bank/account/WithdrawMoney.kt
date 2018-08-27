package br.com.zup.axon.event.bank.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision


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
