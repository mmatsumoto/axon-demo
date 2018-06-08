package br.com.zup.axon.bank.domain.transfer

import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.Money
import br.com.zup.axon.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision

data class RequestTransferMoneyCommand(val transactionId: TransactionId,
                                       val sourceId: AccountId,
                                       val destinationId: AccountId,
                                       val amount: Money)

@Revision("1.0")
data class TransferMoneyRequestedEvent @JsonCreator constructor(
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("sourceId") val sourceId: AccountId,
        @JsonProperty("destinationId") val destinationId: AccountId,
        @JsonProperty("amount") val amount: Money)
