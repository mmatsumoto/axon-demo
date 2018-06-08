package br.com.zup.axon.bank.aggregate

import br.com.zup.axon.bank.domain.transfer.CompleteMoneyTransferCommand
import br.com.zup.axon.bank.domain.transfer.FailMoneyTransferCommand
import br.com.zup.axon.bank.domain.transfer.MoneyTransferCompletedEvent
import br.com.zup.axon.bank.domain.transfer.MoneyTransferFailedEvent
import br.com.zup.axon.bank.domain.transfer.RequestTransferMoneyCommand
import br.com.zup.axon.bank.domain.transfer.TransferMoneyRequestedEvent
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