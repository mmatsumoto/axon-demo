package br.com.zup.axon.command.bank.config

import br.com.zup.axon.command.bank.view.generic.GenericListener
import br.com.zup.axon.command.bank.view.jpa.transfer.BankTransferListener
import br.com.zup.axon.command.bank.view.memory.AccountMemoryListener
import br.com.zup.axon.event.bank.account.upcast.AccountCreatedEventUpcasterV20
import br.com.zup.axon.event.bank.account.upcast.AccountCreatedEventUpcasterV30
import br.com.zup.axon.event.bank.account.upcast.MoneyDepositedEventUpcaster
import org.axonframework.boot.autoconfig.AxonAutoConfiguration
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.distributed.DistributedCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.spring.eventhandling.scheduling.java.SimpleEventSchedulerFactoryBean
import org.axonframework.spring.eventhandling.scheduling.quartz.QuartzEventSchedulerFactoryBean
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import java.util.function.BiFunction
import javax.sql.DataSource

@Configuration
@AutoConfigureAfter(AxonAutoConfiguration::class)
class AxonConfiguration {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Primary
    @Bean("serializer")
    fun serializer(): Serializer = JacksonSerializer()


    // simpleCommandBus only used for sagas
    @Bean("simpleCommandGateway")
    fun simpleCommandGateway(simpleCommandBus: SimpleCommandBus): CommandGateway {
        return DefaultCommandGateway(simpleCommandBus)
    }

    @Bean
    fun snapshotterFactory() =
            SpringAggregateSnapshotterFactoryBean()

    @Bean
    fun eventCountSnapshot(snapshotter: Snapshotter) =
            EventCountSnapshotTriggerDefinition(snapshotter, 5)

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



    /* example to register an interceptor to add some metadata in all commands */
    @Autowired
    fun registerInterceptors(simpleCommandBus: SimpleCommandBus) {
        registerDispatchInterceptor(simpleCommandBus)
    }

    /* example to register an interceptor to add some metadata in all commands */
    @Autowired
    fun registerInterceptors(distributedCommandBus: DistributedCommandBus) {
        registerDispatchInterceptor(distributedCommandBus)
    }

    private fun registerDispatchInterceptor(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor { _ ->
            BiFunction<Int, CommandMessage<*>, CommandMessage<*>> { _, message ->
                message.andMetaData(mapOf("tenant" to "sample_segment"))
            }
        }
    }

    @Autowired
    fun accountMemoryProcessor(configuration: EventProcessingConfiguration) {
        configuration.registerTrackingEventProcessor(AccountMemoryListener.GROUP_NAME)
        configuration.registerTrackingEventProcessor(BankTransferListener.GROUP_NAME)
//        configuration.registerTrackingEventProcessor(GenericListener.GROUP_NAME)
    }


}
