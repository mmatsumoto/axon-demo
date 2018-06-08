package br.com.zup.axon.bank.view.jpa.account

import br.com.zup.axon.bank.aggregate.AccountStatus
import br.com.zup.axon.bank.aggregate.Gender
import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.AccountName
import br.com.zup.axon.bank.domain.account.Money

data class AccountRepresentation(val id: AccountId,
                                 val name: AccountName,
                                 val gender: Gender,
                                 val balance: Money,
                                 val status: AccountStatus)