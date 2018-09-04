package br.com.zup.axon.command.bank.view.jpa.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.AccountName
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.account.AccountStatus
import br.com.zup.axon.event.bank.account.Gender

data class AccountRepresentation(val id: AccountId,
                                 val name: AccountName,
                                 val gender: Gender,
                                 val balance: Money,
                                 val status: AccountStatus)