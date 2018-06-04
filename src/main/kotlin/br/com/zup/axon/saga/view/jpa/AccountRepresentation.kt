package br.com.zup.axon.saga.view.jpa

import br.com.zup.axon.saga.aggregate.AccountId
import br.com.zup.axon.saga.aggregate.AccountName
import br.com.zup.axon.saga.aggregate.Money

data class AccountRepresentation(val id: AccountId, val name: AccountName, val balance: Money)