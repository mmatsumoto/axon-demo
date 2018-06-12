package br.com.zup.axon.bank.aggregate

import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventsourcing.AggregateFactory
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AccountConfiguration(private val eventStore: EventStore,
                           private val snapshotTriggerDefinition: SnapshotTriggerDefinition) {
    @Bean
    fun accountAggregateFactory(): AggregateFactory<Account> = SpringPrototypeAggregateFactory<Account>()
            .apply {
        setPrototypeBeanName("account")
    }

    @Bean("customAccountRepository")
    fun accountRepository(): Repository<Account> =
            EventSourcingRepository<Account>(accountAggregateFactory(), eventStore, snapshotTriggerDefinition)

    @Bean
    fun accountCommandHandler(): AccountCommandHandler =
            AccountCommandHandler(accountRepository(), eventStore)
}
