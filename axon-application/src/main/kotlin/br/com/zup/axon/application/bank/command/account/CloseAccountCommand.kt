package br.com.zup.axon.application.bank.command.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import org.axonframework.commandhandling.TargetAggregateIdentifier


data class CloseAccountCommand(@field:TargetAggregateIdentifier val id: AccountId)
