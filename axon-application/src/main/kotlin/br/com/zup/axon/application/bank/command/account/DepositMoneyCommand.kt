package br.com.zup.axon.application.bank.command.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class DepositMoneyCommand(@field:TargetAggregateIdentifier val accountId: AccountId,
                               val transactionId: TransactionId,
                               val money: Money)
