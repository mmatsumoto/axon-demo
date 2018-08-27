package br.com.zup.axon.application.bank.saga

import br.com.zup.axon.application.bank.command.account.DepositMoneyCommand
import br.com.zup.axon.application.bank.command.account.RefundMoneyCommand
import br.com.zup.axon.application.bank.command.account.WithdrawMoneyCommand
import br.com.zup.axon.application.bank.command.transfer.CompleteMoneyTransferCommand
import br.com.zup.axon.application.bank.command.transfer.FailMoneyTransferCommand
import br.com.zup.axon.application.bank.domain.account.AccountId
import br.com.zup.axon.application.bank.domain.account.Money
import br.com.zup.axon.application.bank.domain.account.TransactionId
import br.com.zup.axon.event.bank.account.AccountNotFoundEvent
import br.com.zup.axon.event.bank.account.MoneyDepositRejectEvent
import br.com.zup.axon.event.bank.account.MoneyDepositedEvent
import br.com.zup.axon.event.bank.account.MoneyRefundedEvent
import br.com.zup.axon.event.bank.account.MoneyWithdrawRejectedEvent
import br.com.zup.axon.event.bank.account.MoneyWithdrawnEvent
import br.com.zup.axon.event.bank.transfer.TransferMoneyRequestedEvent
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
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

    companion object {
        const val BANK_TRANSFER_ASSOCIATION_PROPERTY = "transactionId"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

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
    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
    fun on(event: TransferMoneyRequestedEvent) {
        this.transactionId = event.transactionId
        this.sourceId = event.sourceId
        this.destinationId = event.destinationId
        this.amount = event.amount
        this.withdrawn = false

        commandGateway.send(WithdrawMoneyCommand(event.sourceId, event.transactionId, event.amount), LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
    fun on(event: MoneyWithdrawnEvent) {
        this.withdrawn = true

        commandGateway.send(DepositMoneyCommand(this.destinationId, event.transactionId, event.money),
                            object : CommandCallback<DepositMoneyCommand, AccountId> {
                                override fun onSuccess(message: CommandMessage<out DepositMoneyCommand>, result: TransactionId?) {
                                    logger.info("Command executed successfully: {}", message.commandName)
                                }

                                override fun onFailure(message: CommandMessage<out DepositMoneyCommand>, cause: Throwable?) {
                                    logger.warn("Command resulted in exception: {}", message.commandName, cause)
                                }
                            })

        scheduleEventTimeout(this.destinationId, event.transactionId, event.money)
    }


    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
    fun on(event: AccountNotFoundEvent) {
        when {
            event.id == this.sourceId -> {
                commandGateway.send(FailMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
                SagaLifecycle.end()
            }
            event.id == this.destinationId ->
                commandGateway.send(RefundMoneyCommand(this.sourceId, event.transactionId, this.amount), LoggingCallback.INSTANCE)

        }
    }

    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
    fun on(event: MoneyDepositedEvent) {
        if (this.withdrawn) {
            cancelTimeoutSchedule()
            commandGateway.send(CompleteMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
            SagaLifecycle.end()
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
    fun on(event: MoneyWithdrawRejectedEvent) {
        commandGateway.send(FailMoneyTransferCommand(event.transactionId), LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
    fun on(event: MoneyDepositRejectEvent) {
        commandGateway.send(RefundMoneyCommand(this.sourceId, event.transactionId, event.money), LoggingCallback.INSTANCE)
        scheduleEventTimeout(this.destinationId, event.transactionId, event.money)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = BANK_TRANSFER_ASSOCIATION_PROPERTY)
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

