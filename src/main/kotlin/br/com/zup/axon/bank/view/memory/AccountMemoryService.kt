package br.com.zup.axon.bank.view.memory

import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


interface AccountMemoryService {
    fun deleteToken()
    fun registerTrackingProcessor()
    fun getEvents(): Map<String, Map<String, List<Any>>>
    fun start()
    fun stop()
    fun clean()
    fun insertEvent(id: String, event: Any)
}

@Service
class AccountMemoryServiceImpl(private val config: EventHandlingConfiguration,
                               private val jdbcTemplate: JdbcTemplate): AccountMemoryService {

    private val events = mutableMapOf<String,
            MutableMap<String, MutableList<Any>>>()

    override fun insertEvent(id: String, event: Any) {
        events.computeIfAbsent(id, { mutableMapOf() })
                .computeIfAbsent(event.javaClass.name, { mutableListOf() })
                .add(event)
    }

    override fun clean() {
        events.clear()
    }


    override fun registerTrackingProcessor() {
        config.registerTrackingProcessor(AccountMemoryListener.GROUP_NAME)
    }

    override fun deleteToken() {
        jdbcTemplate.update("DELETE FROM token_entry where processor_name = ? ", AccountMemoryListener.GROUP_NAME)
    }

    override fun getEvents(): Map<String, Map<String, List<Any>>> = events
            .map { it.key to it.value.toMap()
                    .map { it.key to it.value.toList() }.toMap() }
            .toMap()

    override fun stop() {
        config.processors.filter(filterByGroup())
                .forEach { it.shutDown() }
    }

    private fun filterByGroup(): (EventProcessor) -> Boolean =
            { it.name == AccountMemoryListener.GROUP_NAME }

    override fun start() {
        config.processors.filter { it.name == AccountMemoryListener.GROUP_NAME }
                .forEach { it.start() }
    }



}