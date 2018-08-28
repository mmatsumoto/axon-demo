package br.com.zup.axon.application.bank.controller

import br.com.zup.axon.application.bank.view.memory.AccountMemoryService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/accounts/memory", produces = [MediaType.APPLICATION_JSON_VALUE])
class AccountMemoryController(private val accountMemoryService: AccountMemoryService) {

    @GetMapping
    fun findInMemory() = accountMemoryService.getEvents()

    @PostMapping("/stop")
    fun stopInMemory() = accountMemoryService.stop()

    @PostMapping("/start")
    fun startInMemory() = accountMemoryService.start()

    @PostMapping("/replay")
    fun resetInMemory() = accountMemoryService.replay()

    @DeleteMapping
    fun cleanMemory() = accountMemoryService.clean()
}
