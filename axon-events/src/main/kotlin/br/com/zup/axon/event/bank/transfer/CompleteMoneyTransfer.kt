package br.com.zup.axon.event.bank.transfer

import br.com.zup.axon.application.bank.domain.account.TransactionId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision

@Revision("1.0")
data class MoneyTransferCompletedEvent @JsonCreator constructor(
        @JsonProperty("transactionId") val transactionId: TransactionId)
