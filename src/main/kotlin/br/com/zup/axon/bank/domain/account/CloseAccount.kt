package br.com.zup.axon.bank.domain.account

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision


data class CloseAccountCommand(@field:TargetAggregateIdentifier val id: AccountId)

@Revision("1.0")
data class AccountClosedEvent @JsonCreator constructor(@JsonProperty("accountId") val accountId: AccountId)

@Revision("1.0")
data class AccountCloseRejectEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("balance") val balance: Money)

