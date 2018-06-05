package br.com.zup.axon.bank.controller

import br.com.zup.axon.bank.aggregate.Account
import br.com.zup.axon.bank.aggregate.AccountId
import br.com.zup.axon.bank.aggregate.Gender
import br.com.zup.axon.bank.aggregate.Money
import br.com.zup.axon.bank.event.CloseAccountCommand
import br.com.zup.axon.bank.event.CreateAccountCommand
import br.com.zup.axon.bank.event.DepositMoneyCommand
import br.com.zup.axon.bank.event.RequestTransferMoneyCommand
import br.com.zup.axon.bank.event.WithdrawMoneyCommand
import br.com.zup.axon.bank.view.jpa.AccountService
import br.com.zup.axon.bank.view.jpa.BankTransferService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/transfers",
                produces = [MediaType.APPLICATION_JSON_VALUE])
class BankTransferController(
        private val bankTransferService: BankTransferService) {

    @GetMapping
    fun findAll() = bankTransferService.findAll()

    @GetMapping(path = ["{id}"])
    fun findOne(@PathVariable id: AccountId) =
            bankTransferService.findOne(id)?.let { ResponseEntity(it, HttpStatus.OK) }
                    ?: ResponseEntity(HttpStatus.NOT_FOUND)

}
