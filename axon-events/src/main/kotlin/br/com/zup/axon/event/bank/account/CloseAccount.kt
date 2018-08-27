package br.com.zup.axon.event.bank.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision


@Revision("1.0")
data class AccountClosedEvent @JsonCreator constructor(@JsonProperty("accountId") val accountId: AccountId)

@Revision("1.0")
data class AccountCloseRejectEvent @JsonCreator constructor(
        @JsonProperty("accountId") val accountId: AccountId,
        @JsonProperty("balance") val balance: Money)

