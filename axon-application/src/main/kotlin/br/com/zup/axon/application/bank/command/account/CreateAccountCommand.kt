package br.com.zup.axon.application.bank.command.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.AccountName
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.account.Gender
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateAccountCommand(@field:TargetAggregateIdentifier
                                val id: AccountId,
                                val name: AccountName,
                                val gender: Gender,
                                val money: Money)
