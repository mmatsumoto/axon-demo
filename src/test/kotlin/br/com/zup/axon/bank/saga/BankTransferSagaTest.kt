package br.com.zup.axon.bank.saga

import br.com.zup.axon.bank.aggregate.Money
import br.com.zup.axon.bank.event.CompleteMoneyTransferCommand
import br.com.zup.axon.bank.event.DepositMoneyCommand
import br.com.zup.axon.bank.event.FailMoneyTransferCommand
import br.com.zup.axon.bank.event.MoneyDepositRejectEvent
import br.com.zup.axon.bank.event.MoneyDepositedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawRejectedEvent
import br.com.zup.axon.bank.event.MoneyWithdrawnEvent
import br.com.zup.axon.bank.event.TransferMoneyRequestedEvent
import br.com.zup.axon.bank.event.WithdrawMoneyCommand
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test

class BankTransferSagaTest {
    private lateinit var fixture: SagaTestFixture<BankTransferSaga>
    private val tenant = "sample_segment"

    @Before
    fun setUp() {
        fixture = SagaTestFixture(BankTransferSaga::class.java)
    }

    @Test
    fun `on TransferMoneyRequestedEvent`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenNoPriorActivity()
                .whenAggregate(transactionId)
                .publishes(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount))
                .expectActiveSagas(1)
                .expectDispatchedCommands(WithdrawMoneyCommand(sourceId, transactionId, amount))
    }

    @Test
    fun `on MoneyWithdrawnEvent`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenAggregate(transactionId)
                    .published(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount))
                .whenAggregate(sourceId)
                    .publishes(MoneyWithdrawnEvent(sourceId, transactionId, amount, 100))
                .expectActiveSagas(1)
                .expectDispatchedCommands(DepositMoneyCommand(destinationId, transactionId, amount))
    }

    @Test
    fun `on MoneyDepositedEvent`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenAggregate(transactionId)
                    .published(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount))

                .andThenAggregate(sourceId)
                    .published(MoneyWithdrawnEvent(sourceId, transactionId, amount, 100))

                .whenAggregate(destinationId)
                    .publishes(MoneyDepositedEvent(destinationId, transactionId, amount, 100, tenant))

                .expectActiveSagas(0)
                    .expectDispatchedCommands(CompleteMoneyTransferCommand(transactionId))
    }


    @Test
    fun `on MoneyWithdrawRejectedEvent`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenAggregate(transactionId)
                .published(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount))

                .whenAggregate(sourceId)
                .publishes(MoneyWithdrawRejectedEvent(sourceId, transactionId, amount, 100))

                .expectActiveSagas(0)
                .expectDispatchedCommands(FailMoneyTransferCommand(transactionId))
    }

    @Test
    fun `on MoneyDepositRejectedEvent`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenAggregate(transactionId)
                .published(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount))

                .andThenAggregate(sourceId)
                    .published(MoneyWithdrawnEvent(sourceId, transactionId, amount, 100))

                .whenAggregate(destinationId)
                    .publishes(MoneyDepositRejectEvent(destinationId, transactionId, amount))

                .expectActiveSagas(1)
                .expectDispatchedCommands(DepositMoneyCommand(sourceId, transactionId, amount))
    }

    @Test
    fun `on refund deposit`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenAggregate(transactionId)
                .published(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount))

                .andThenAggregate(sourceId)
                    .published(MoneyWithdrawnEvent(sourceId, transactionId, amount, 100))

                .andThenAggregate(destinationId)
                    .published(MoneyDepositRejectEvent(destinationId, transactionId, amount))

                .whenAggregate(sourceId)
                    .publishes(MoneyDepositedEvent(sourceId, transactionId, amount, 100, tenant))

                .expectActiveSagas(0)
                .expectDispatchedCommands(FailMoneyTransferCommand(transactionId))
    }

    @Test
    fun `on refund deposidt`() {
        val transactionId = "txt1"
        val sourceId =  "acc1"
        val destinationId =  "acc2"
        val amount: Money = 100

        fixture.givenAggregate(transactionId)
                .published(TransferMoneyRequestedEvent(transactionId, sourceId, destinationId, amount),
                           MoneyWithdrawnEvent(sourceId, transactionId, amount, 100),
                           MoneyDepositRejectEvent(destinationId, transactionId, amount))
                .whenPublishingA(MoneyDepositedEvent(sourceId, transactionId, amount, 100, tenant))

                .expectActiveSagas(0)
                .expectDispatchedCommands(FailMoneyTransferCommand(transactionId))
    }

}