package br.com.zup.axon.command.bank.controller.form

import br.com.zup.axon.application.bank.domain.account.AccountName
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.event.bank.account.Gender


data class AccountRequest(val name: AccountName, val money: Money, val gender: Gender? = null)
