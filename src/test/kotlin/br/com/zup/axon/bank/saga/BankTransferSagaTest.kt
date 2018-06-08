package br.com.zup.axon.bank.saga

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
                .expectDispatchedCommands(RefundMoneyCommand(sourceId, transactionId, amount))
    }

    @Test
    fun `on refund money`() {
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
                    .publishes(MoneyRefundedEvent(sourceId, transactionId, amount, 100))

                .expectActiveSagas(0)
                .expectDispatchedCommands(FailMoneyTransferCommand(transactionId))
    }

}