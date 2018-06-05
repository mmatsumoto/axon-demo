package br.com.zup.axon.bank.aggregate

import br.com.zup.axon.bank.event.CompleteMoneyTransferCommand
import br.com.zup.axon.bank.event.FailMoneyTransferCommand
import br.com.zup.axon.bank.event.MoneyTransferCompletedEvent
import br.com.zup.axon.bank.event.MoneyTransferFailedEvent
import br.com.zup.axon.bank.event.RequestTransferMoneyCommand
import br.com.zup.axon.bank.event.TransferMoneyRequestedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test

class BankTransferTest {

    private lateinit var fixture: AggregateTestFixture<BankTransfer>

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(BankTransfer::class.java)
    }

    @Test
    fun `on RequestTransferMoneyCommand`() {
        fixture.givenNoPriorActivity()
                .`when`(RequestTransferMoneyCommand("tx1", "acc1", "acc2", 100))
                .expectEvents(TransferMoneyRequestedEvent("tx1", "acc1", "acc2", 100))
    }


    @Test
    fun `on CompleteMoneyTransferCommand`() {
        fixture.given(TransferMoneyRequestedEvent("tx1", "acc1", "acc2", 100))
                .`when`(CompleteMoneyTransferCommand("tx1"))
                .expectEvents(MoneyTransferCompletedEvent("tx1"))
    }

    @Test
    fun `on FailMoneyTransferCommand`() {
        fixture.given(TransferMoneyRequestedEvent("tx1", "acc1", "acc2", 100))
                .`when`(FailMoneyTransferCommand("tx1"))
                .expectEvents(MoneyTransferFailedEvent("tx1"))
    }



}