package br.com.zup.axon.bank.view.jpa

import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.AccountName
import br.com.zup.axon.bank.aggregate.Money

data class AccountRepresentation(val id: AccountId, val name: AccountName, val balance: Money)