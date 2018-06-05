package br.com.zup.axon.bank.controller

import br.com.zup.axon.bank.aggregate.AccountName
import br.com.zup.axon.bank.aggregate.Gender
import br.com.zup.axon.bank.aggregate.Money


data class AccountRequest(val name: AccountName, val money: Money, val gender: Gender? = null)