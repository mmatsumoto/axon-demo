package br.com.zup.axon.command.bank.controller

import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.command.bank.view.jpa.transfer.BankTransferService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/transfers", produces = [MediaType.APPLICATION_JSON_VALUE])
class BankTransferController(
        private val bankTransferService: BankTransferService) {

    @GetMapping
    fun findAll() = bankTransferService.findAll()

    @GetMapping(path = ["{id}"])
    fun findOne(@PathVariable id: AccountId) = bankTransferService.findOne(id)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
                    ?: ResponseEntity(HttpStatus.NOT_FOUND)

}
