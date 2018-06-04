package br.com.zup.axon.saga.config

import br.com.zup.axon.saga.event.upcast.AccountCreatedEventUpcasterV20
import br.com.zup.axon.saga.event.upcast.AccountCreatedEventUpcasterV30
import br.com.zup.axon.saga.event.upcast.AccountSnapshotUpcasterV20
import br.com.zup.axon.saga.event.upcast.MoneyDepositedEventUpcaster
import br.com.zup.axon.saga.view.memory.AccountMemoryService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.eventhandling.saga.repository.jpa.JpaSagaStore
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.function.BiFunction
import javax.sql.DataSource

@Configuration
class AxonConfiguration {
    @Primary
    @Bean("serializer")
    fun serializer(): Serializer = JacksonSerializer()

    @Autowired
    fun objectMapper(mapper: ObjectMapper) {
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Autowired
    fun registerInterceptors(commandBus: CommandBus) {
        if (commandBus is SimpleCommandBus) {
            commandBus.registerDispatchInterceptor { _ ->
                BiFunction<Int, CommandMessage<*>, CommandMessage<*>> { _, message ->
                    message.andMetaData(mapOf("tenant" to "sample_segment"))
                }
            }
        }
    }


    @Autowired
    fun registerGroupProcessors(accountMemoryService: AccountMemoryService) {
        accountMemoryService.deleteToken()
        accountMemoryService.registerTrackingProcessor()
    }

    @Bean
    fun upcasterChain(): EventUpcasterChain =
            EventUpcasterChain(AccountSnapshotUpcasterV20(),
                               AccountCreatedEventUpcasterV20(),
                               AccountCreatedEventUpcasterV30(),
                               MoneyDepositedEventUpcaster())

    @Bean
    fun eventStorageEngine(serializer: Serializer,
                           upcasterChain: EventUpcasterChain,
                           dataSource: DataSource,
                           entityManagerProvider: EntityManagerProvider,
                           transactionManager: TransactionManager): JpaEventStorageEngine {
        return JpaEventStorageEngine(serializer, upcasterChain, dataSource, entityManagerProvider, transactionManager)
    }

    @Bean
    fun sagaStore(serializer: Serializer,
                  entityManagerProvider: EntityManagerProvider): JpaSagaStore {
        return JpaSagaStore(serializer, entityManagerProvider)
    }

    @Bean
    fun snapshotterFactory(): SpringAggregateSnapshotterFactoryBean {
        return SpringAggregateSnapshotterFactoryBean()
    }

    @Bean
    fun eventCountSnapshot(snapshotter: Snapshotter) =
            EventCountSnapshotTriggerDefinition(snapshotter, 5)

    //    @Bean
//    @Scope("prototype")
//    fun elementAggregate(): Account = Account()
//
//    @Bean
//    fun accountAggregateFactory(): AggregateFactory<Account> {
//        return SpringPrototypeAggregateFactory<Account>()
//                .apply { setPrototypeBeanName("account") }
//    }
//
//    @Bean
//    @Primary
//    fun snapshotterAccountRepository(eventStore: EventStore,
//                          accountAggregateFactory: AggregateFactory<Account>,
//                          snapshotter: Snapshotter): Repository<Account> {
//        return EventSourcingRepository<Account>(Account::class.java,
//                                                eventStore,
//                                                EventCountSnapshotTriggerDefinition(snapshotter, 2))
//    }
}


