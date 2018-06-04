package br.com.zup.axon.saga.view.jpa

import br.com.zup.axon.saga.aggregate.AccountId
import org.springframework.data.jpa.repository.JpaRepository


interface AccountRepository : JpaRepository<AccountEntity, AccountId>
