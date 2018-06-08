package br.com.zup.axon.bank.view.jpa.account

import br.com.zup.axon.bank.domain.account.AccountId
import org.springframework.data.jpa.repository.JpaRepository


interface AccountRepository : JpaRepository<AccountEntity, AccountId>
