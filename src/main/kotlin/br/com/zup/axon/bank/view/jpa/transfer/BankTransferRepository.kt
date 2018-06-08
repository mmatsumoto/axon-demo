package br.com.zup.axon.bank.view.jpa.transfer

import org.springframework.data.jpa.repository.JpaRepository


interface BankTransferRepository : JpaRepository<BankTransferEntity, String>
