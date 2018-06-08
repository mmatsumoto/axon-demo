package br.com.zup.axon.bank.controller.form

import br.com.zup.axon.bank.aggregate.Gender
import br.com.zup.axon.bank.domain.account.AccountName
import br.com.zup.axon.bank.domain.account.Money


data class AccountRequest(val name: AccountName, val money: Money, val gender: Gender? = null)
