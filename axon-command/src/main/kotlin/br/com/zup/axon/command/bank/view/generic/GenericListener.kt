package br.com.zup.axon.command.bank.view.generic

import br.com.zup.axon.command.bank.view.generic.GenericListener.Companion.GROUP_NAME
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@ProcessingGroup(GROUP_NAME)
class GenericListener {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @EventHandler
    fun on(event: EventMessage<Any>) {
        log.info("""
-------------------------------
    payloadType: ${event.payloadType}
    metaData   : ${event.metaData}
    payload    : ${event.payload}
    timestamp  : ${event.timestamp}
-------------------------------""")
    }


    companion object {
        const val GROUP_NAME = "GenericListenerGroup"
    }

}