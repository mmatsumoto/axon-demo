package br.com.zup.axon.application.bank.aggregate

import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventsourcing.AggregateFactory
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * Everything in this Configuration is optional.
 * It's just a simple example to configure a custom CommandHandler, AggregateFactory, Repository and Snapshot
 *
 * Generally speaking, you could just receive the Commands directly at the Aggregate itself and use everything
 * that was auto-configured by axon-springboot.
 *
 * @see BankTransfer
 */
@Configuration
class AccountConfiguration(private val eventStore: EventStore,
                           private val snapshotter: Snapshotter) {
    @Bean
    fun accountAggregateFactory(): AggregateFactory<Account> = SpringPrototypeAggregateFactory<Account>()
            .apply {
                setPrototypeBeanName("account")
            }

    @Bean
    fun accountSnapshotTrigger(): SnapshotTriggerDefinition =
            EventCountSnapshotTriggerDefinition(snapshotter, 10)


    @Bean("customAccountRepository")
    fun accountRepository(): Repository<Account> =
            EventSourcingRepository<Account>(accountAggregateFactory(), eventStore, accountSnapshotTrigger())

    @Bean
    fun accountCommandHandler(): AccountCommandHandler =
            AccountCommandHandler(accountRepository(), eventStore)
}
