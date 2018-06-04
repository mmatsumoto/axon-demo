package br.com.zup.axon.saga.controller

import br.com.zup.axon.saga.aggregate.AccountName
import br.com.zup.axon.saga.aggregate.Gender
import br.com.zup.axon.saga.aggregate.Money


data class AccountRequest(val name: AccountName, val money: Money, val gender: Gender? = null)