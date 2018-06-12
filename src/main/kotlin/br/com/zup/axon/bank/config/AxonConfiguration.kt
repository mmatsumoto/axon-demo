package br.com.zup.axon.bank.config

import br.com.zup.axon.bank.domain.account.upcast.AccountCreatedEventUpcasterV20
import br.com.zup.axon.bank.domain.account.upcast.AccountCreatedEventUpcasterV30
import br.com.zup.axon.bank.domain.account.upcast.MoneyDepositedEventUpcaster
import br.com.zup.axon.bank.view.jpa.transfer.BankTransferService
import br.com.zup.axon.bank.view.memory.AccountMemoryService
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.spring.eventhandling.scheduling.java.SimpleEventSchedulerFactoryBean
import org.axonframework.spring.eventhandling.scheduling.quartz.QuartzEventSchedulerFactoryBean
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import java.util.function.BiFunction
import javax.sql.DataSource

@Configuration
class AxonConfiguration {

    @Primary
    @Bean("serializer")
    fun serializer(): Serializer = JacksonSerializer()

    /* example to register an interceptor to add some metadata in all commands */
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

    @Bean
    fun snapshotterFactory() =
            SpringAggregateSnapshotterFactoryBean()

    @Bean
    fun eventCountSnapshot(snapshotter: Snapshotter) =
            EventCountSnapshotTriggerDefinition(snapshotter, 5)

    @Autowired
    fun registerGroupProcessors(accountMemoryService: AccountMemoryService,
                                bankTransferService: BankTransferService) {
        accountMemoryService.deleteToken() // to replay everything again on every boot
        accountMemoryService.registerTrackingProcessor()

        bankTransferService.deleteToken()
        bankTransferService.registerTrackingProcessor()
    }

    @Bean
    fun upcasterChain(): EventUpcasterChain =
            EventUpcasterChain(AccountCreatedEventUpcasterV20(),
                               AccountCreatedEventUpcasterV30(),
                               MoneyDepositedEventUpcaster())

    @Bean
    fun eventStorageEngine(serializer: Serializer,
                           upcasterChain: EventUpcasterChain,
                           dataSource: DataSource,
                           entityManagerProvider: EntityManagerProvider,
                           transactionManager: TransactionManager): JpaEventStorageEngine =
            JpaEventStorageEngine(serializer, upcasterChain, dataSource, entityManagerProvider, transactionManager)


    @Bean("simpleScheduler")
    fun simpleEventScheduler(transactionManager: PlatformTransactionManager) = SimpleEventSchedulerFactoryBean().apply {
        setTransactionManager(transactionManager)
    }

    @Bean("quartzScheduler")
    fun quartzEventScheduler(transactionManager: PlatformTransactionManager) = QuartzEventSchedulerFactoryBean().apply {
        setTransactionManager(transactionManager)
    }



}


