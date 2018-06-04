package br.com.zup.axon.saga.controller

import br.com.zup.axon.saga.aggregate.Account
import br.com.zup.axon.saga.aggregate.AccountId
import br.com.zup.axon.saga.aggregate.Gender
import br.com.zup.axon.saga.aggregate.Money
import br.com.zup.axon.saga.event.CreateAccountCommand
import br.com.zup.axon.saga.event.DepositMoneyCommand
import br.com.zup.axon.saga.event.WithdrawMoneyCommand
import br.com.zup.axon.saga.view.jpa.AccountService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/accounts",
                consumes = [MediaType.APPLICATION_JSON_VALUE],
                produces = [MediaType.APPLICATION_JSON_VALUE])
class AccountController(private val commandGateway: CommandGateway,
                        private val accountService: AccountService) {


    @PostMapping
    fun create(@RequestBody account: AccountRequest): IdRepresentation =
            commandGateway.sendAndWait<AccountId>(
                    CreateAccountCommand(Account.newId(), account.name, account.gender ?: Gender.UNKNOWN, account.money))
                    .let (::IdRepresentation)


    @PutMapping(path = ["{id}/deposit/{money}"])
    fun deposit(@PathVariable id: AccountId,
                @PathVariable money: Money): TransactionRepresentation =
            commandGateway.sendAndWait<AccountId>(DepositMoneyCommand(id, UUID.randomUUID().toString(), money))
                    .let (::TransactionRepresentation)

    @PutMapping(path = ["{id}/withdraw/{money}"])
    fun withdraw(@PathVariable id: AccountId,
                 @PathVariable money: Money): TransactionRepresentation =
            commandGateway.sendAndWait<AccountId>(WithdrawMoneyCommand(id, UUID.randomUUID().toString(), money))
                    .let (::TransactionRepresentation)


    @GetMapping
    fun findAll() = accountService.findAll()

    @GetMapping(path = ["{id}"])
    fun findOne(@PathVariable id: AccountId) =
            accountService.findOne(id)?.let { ResponseEntity(it, HttpStatus.OK) }
                    ?: ResponseEntity(HttpStatus.NOT_FOUND)
}
