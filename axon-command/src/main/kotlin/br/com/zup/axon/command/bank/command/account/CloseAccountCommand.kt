package br.com.zup.axon.command.bank.command.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier


data class CloseAccountCommand @JsonCreator constructor(@field:TargetAggregateIdentifier
                                                        @JsonProperty("id") val id: AccountId)
