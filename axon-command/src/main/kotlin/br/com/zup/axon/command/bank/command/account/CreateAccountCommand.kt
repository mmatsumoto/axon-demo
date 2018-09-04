package br.com.zup.axon.command.bank.command.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.AccountName
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.account.Gender
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateAccountCommand @JsonCreator constructor(@field:TargetAggregateIdentifier
                                                         @JsonProperty("id") val id: AccountId,
                                                         @JsonProperty("name") val name: AccountName,
                                                         @JsonProperty("gender") val gender: Gender,
                                                         @JsonProperty("money") val money: Money)
