package br.com.zup.axon.command.bank.view.jpa.transfer

import org.springframework.data.jpa.repository.JpaRepository


interface BankTransferRepository : JpaRepository<BankTransferEntity, String>
