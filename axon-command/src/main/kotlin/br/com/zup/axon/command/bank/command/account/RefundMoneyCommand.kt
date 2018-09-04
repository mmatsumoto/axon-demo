package br.com.zup.axon.command.bank.command.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class RefundMoneyCommand @JsonCreator constructor(@field:TargetAggregateIdentifier
                                                       @JsonProperty("accountId") val accountId: AccountId,
                                                       @JsonProperty("transactionId") val transactionId: TransactionId,
                                                       @JsonProperty("money") val money: Money)
