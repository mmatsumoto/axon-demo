package br.com.zup.axon.bank.view.jpa

import org.springframework.data.jpa.repository.JpaRepository


interface BankTransferRepository : JpaRepository<BankTransferEntity, String>
