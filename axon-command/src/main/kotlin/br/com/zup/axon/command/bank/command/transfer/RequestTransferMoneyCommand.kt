package br.com.zup.axon.command.bank.command.transfer

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RequestTransferMoneyCommand @JsonCreator constructor(@JsonProperty("transactionId") val transactionId: TransactionId,
                                                                @JsonProperty("sourceId") val sourceId: AccountId,
                                                                @JsonProperty("destinationId") val destinationId: AccountId,
                                                                @JsonProperty("amount") val amount: Money)

