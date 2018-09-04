package br.com.zup.axon.command.bank.view.memory

import br.com.zup.axon.command.bank.config.helper.trackingEventProcessor
import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.EventMessage
import org.axonframework.messaging.InterceptorChain
import org.axonframework.messaging.MessageHandlerInterceptor
import org.axonframework.messaging.unitofwork.UnitOfWork
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


interface AccountMemoryService {
    fun getEvents(): Map<String, Map<String, List<Any>>>
    fun start()
    fun stop()
    fun replay()
    fun clean()
    fun insertEvent(id: String, event: Any)
}

@Service
class AccountMemoryServiceImpl(private val configuration: EventProcessingConfiguration) : AccountMemoryService {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val events = mutableMapOf<String,
            MutableMap<String, MutableList<Any>>>()

    override fun insertEvent(id: String, event: Any) {
        events.computeIfAbsent(id) { mutableMapOf() }
                .computeIfAbsent(event.javaClass.name) { mutableListOf() }
                .add(event)
    }

    override fun clean() {
        events.clear()
    }

    override fun getEvents(): Map<String, Map<String, List<Any>>> = events
            .map { event ->
                event.key to event.value.map { it.key to it.value.toList() }.toMap()
            }
            .toMap()

    override fun start() {
        configuration.trackingEventProcessor(AccountMemoryListener.GROUP_NAME) {
            it.start()
        }
    }

    override fun stop() {
        configuration.trackingEventProcessor(AccountMemoryListener.GROUP_NAME) {
            it.shutDown()
        }
    }

    override fun replay() {

        configuration.trackingEventProcessor(AccountMemoryListener.GROUP_NAME) {
            it.shutDown()

//            while (it.isRunning) {
//                log.info("Waiting the shutdown process to end tracking:$it")
//                Thread.sleep(1000)
//            }
//

//            it.registerInterceptor(object : MessageHandlerInterceptor<EventMessage<*>> {
//                override fun handle(unitOfWork: UnitOfWork<out EventMessage<*>>?, interceptorChain: InterceptorChain?): Any {
//
//                    unitOfWork?.transformMessage { m ->
//                        m.andMetaData(mutableMapOf("replay" to true))
//                    }
//
//                    return interceptorChain!!.proceed()
//                }
//            })

                    log.info("supportsReset: " + it.supportsReset())

            it.resetTokens()
            it.start()
        }
    }



}