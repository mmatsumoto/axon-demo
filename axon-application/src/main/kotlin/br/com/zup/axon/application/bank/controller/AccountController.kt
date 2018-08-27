package br.com.zup.axon.application.bank.controller

import br.com.zup.axon.application.bank.aggregate.Account
import br.com.zup.axon.application.bank.command.account.CloseAccountCommand
import br.com.zup.axon.application.bank.command.account.CreateAccountCommand
import br.com.zup.axon.application.bank.command.account.DepositMoneyCommand
import br.com.zup.axon.application.bank.command.account.WithdrawMoneyCommand
import br.com.zup.axon.application.bank.command.transfer.RequestTransferMoneyCommand
import br.com.zup.axon.application.bank.controller.form.AccountRequest
import br.com.zup.axon.application.bank.controller.representation.IdRepresentation
import br.com.zup.axon.application.bank.controller.representation.TransactionRepresentation
import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.view.jpa.account.AccountService
import br.com.zup.axon.event.bank.account.Gender
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
@RequestMapping("/accounts",
                consumes = [MediaType.APPLICATION_JSON_VALUE],
                produces = [MediaType.APPLICATION_JSON_VALUE])
class AccountController(private val commandGateway: CommandGateway,
                        private val accountService: AccountService) {

    @PostMapping
    fun create(@RequestBody account: AccountRequest): IdRepresentation =
            commandGateway.sendAndWait<AccountId>(
                    CreateAccountCommand(Account.newId(), account.name, account.gender
                            ?: Gender.UNKNOWN, account.money))
                    .let(::IdRepresentation)


    @PutMapping(path = ["{id}/deposit/{money}"])
    fun deposit(@PathVariable id: AccountId,
                @PathVariable money: Money): TransactionRepresentation =
            commandGateway.sendAndWait<AccountId>(DepositMoneyCommand(id, UUID.randomUUID().toString(), money))
                    .let(::TransactionRepresentation)

    @PutMapping(path = ["{id}/withdraw/{money}"])
    fun withdraw(@PathVariable id: AccountId,
                 @PathVariable money: Money): TransactionRepresentation =
            commandGateway.sendAndWait<AccountId>(WithdrawMoneyCommand(id, UUID.randomUUID().toString(), money))
                    .let(::TransactionRepresentation)

    @PutMapping(path = ["/transfer/{sourceId}/{destinationId}/{money}"])
    fun transfer(@PathVariable sourceId: AccountId,
                 @PathVariable destinationId: AccountId,
                 @PathVariable money: Money): TransactionRepresentation =
            commandGateway.sendAndWait<AccountId>(
                    RequestTransferMoneyCommand(UUID.randomUUID().toString(), sourceId, destinationId, money))
                    .let(::TransactionRepresentation)

    @DeleteMapping("{accountId}")
    fun closeAccount(@PathVariable accountId: AccountId): TransactionRepresentation {
        return commandGateway.sendAndWait<AccountId>(CloseAccountCommand(accountId))
                .let { TransactionRepresentation(accountId) }
    }

    @GetMapping
    fun findAll() = accountService.findAll()

    @GetMapping(path = ["{id}"])
    fun findOne(@PathVariable id: AccountId) = accountService.findOne(id)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
}
