package br.com.zup.axon.kafka.consumer.listener

import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.EventMessage
import org.axonframework.eventhandling.ReplayStatus
import org.axonframework.messaging.MetaData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("GenericKafkaConsumerListener")
class GenericListener {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @EventHandler
    fun on(event: EventMessage<Any>, metaData: MetaData) {
        log.info("""
GenericListener----------------
    payloadType:    ${event.payloadType}
    payload    :    ${event.payload}
    timestamp  :    ${event.timestamp}
    metaData:       $metaData
-------------------------------""")
    }

}