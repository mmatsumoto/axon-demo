package br.com.zup.axon.command.bank.view.jpa.account

import br.com.zup.axon.application.bank.domain.account.AccountId
import org.springframework.data.jpa.repository.JpaRepository


interface AccountRepository : JpaRepository<AccountEntity, AccountId>
