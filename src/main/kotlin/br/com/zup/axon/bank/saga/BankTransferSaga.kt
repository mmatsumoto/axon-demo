package br.com.zup.axon.bank.saga

import br.com.zup.axon.bank.domain.account.AccountId
import br.com.zup.axon.bank.domain.account.DepositMoneyCommand
import br.com.zup.axon.bank.domain.account.Money
import br.com.zup.axon.bank.domain.account.MoneyDepositRejectEvent
import br.com.zup.axon.bank.domain.account.MoneyDepositedEvent
import br.com.zup.axon.bank.domain.account.MoneyRefundedEvent
import br.com.zup.axon.bank.domain.account.MoneyWithdrawRejectedEvent
import br.com.zup.axon.bank.domain.account.MoneyWithdrawnEvent
import br.com.zup.axon.bank.domain.account.RefundMoneyCommand
import br.com.zup.axon.bank.domain.account.WithdrawMoneyCommand
import br.com.zup.axon.bank.domain.transfer.CompleteMoneyTransferCommand
import br.com.zup.axon.bank.domain.transfer.FailMoneyTransferCommand
import br.com.zup.axon.bank.domain.transfer.TransferMoneyRequestedEvent
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.model.AggregateNotFoundException
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.eventhandling.scheduling.EventScheduler
import org.axonframework.eventhandling.scheduling.ScheduleToken
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import java.time.Duration

@Saga
@Revision("1.0")
final class BankTransferSaga {

    private val logger = LoggerFactory.getLogger(LoggingCallback::class.java)

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @Autowired
    @Qualifier("quartzScheduler")
    @Transient
    private lateinit var scheduler: EventScheduler

    lateinit var transactionId: String
        private set

    lateinit var sourceId: AccountId
        private set

    lateinit var destinationId: AccountId
        private set

    var amount: Money = 0L
        private set

    var withdrawn:Boolean = false

    var timeoutToken: ScheduleToken? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: TransferMoneyRequestedEvent) {
        this.transactionId = event.transactionId
        this.sourceId = event.sourceId
        this.destinationId = event.destinationId
        this.amount = event.amount
        this.withdrawn = false

        commandGateway.send(WithdrawMoneyCommand(event.sourceId, event.transactionId, event.amount),
                            object : CommandCallback<WithdrawMoneyCommand, AccountId> {
                                override fun onSuccess(message: CommandMessage<out WithdrawMoneyCommand>, result: AccountId) {
                                    logger.info("Command executed successfully: {}", message.commandName)
                                }

                                override fun onFailure(message: CommandMessage<out WithdrawMoneyCommand>, cause: Throwable) {
                                    logger.warn("Command resulted in exception: {}", message.commandName, cause)
                                    if (cause is AggregateNotFoundException) {
                                        commandGateway.send(FailMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
                                        SagaLifecycle.end()
                                    }
                                }
                            })

    }

    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyWithdrawnEvent) {
        this.withdrawn = true

        commandGateway.send(DepositMoneyCommand(this.destinationId, event.transactionId, event.money),
                            object : CommandCallback<DepositMoneyCommand, AccountId> {
                                override fun onSuccess(message: CommandMessage<out DepositMoneyCommand>, result: AccountId) {
                                    logger.info("Command executed successfully: {}", message.commandName)
                                }

                                override fun onFailure(message: CommandMessage<out DepositMoneyCommand>, cause: Throwable) {
                                    logger.warn("Command resulted in exception: {}", message.commandName, cause)
                                    if (cause is AggregateNotFoundException) {
                                        commandGateway.send(RefundMoneyCommand(sourceId,
                                                                               event.transactionId,
                                                                               event.money),
                                                            LoggingCallback.INSTANCE)
                                    }
                                }
                            })

        scheduleEventTimeout(this.destinationId, event.transactionId, event.money)
    }

    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyDepositedEvent) {
        if (this.withdrawn) {
            cancelTimeoutSchedule()
            commandGateway.send(CompleteMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
            SagaLifecycle.end()
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyWithdrawRejectedEvent) {
        commandGateway.send(FailMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyDepositRejectEvent) {
        commandGateway.send(RefundMoneyCommand(this.sourceId, event.transactionId, event.money), LoggingCallback.INSTANCE)
        scheduleEventTimeout(this.destinationId, event.transactionId, event.money)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyRefundedEvent) {
        cancelTimeoutSchedule()
        commandGateway.send(FailMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
    }

    private fun scheduleEventTimeout(destinationId: AccountId, transactionId: String, money: Money) =
            MoneyDepositRejectEvent(destinationId, transactionId, money)
                    .also { cancelTimeoutSchedule() }
                    .also { logger.info("Scheduling MoneyDepositRejectEvent: $it") }
                    .let { scheduler.schedule(Duration.ofSeconds(15), it) }
                    .also { this.timeoutToken = it }

    private fun cancelTimeoutSchedule() = this.timeoutToken?.run {
        scheduler.cancelSchedule(this)
                .also { logger.info("Canceling scheduler timeout $this") }
    }

}

