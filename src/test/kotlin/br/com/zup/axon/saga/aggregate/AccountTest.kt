package br.com.zup.axon.saga.aggregate

import br.com.zup.axon.saga.event.AccountCreatedEvent
import br.com.zup.axon.saga.event.CreateAccountCommand
import br.com.zup.axon.saga.event.DepositMoneyCommand
import br.com.zup.axon.saga.event.MoneyDepositedEvent
import br.com.zup.axon.saga.event.MoneyWithdrawRejectedEvent
import br.com.zup.axon.saga.event.MoneyWithdrawnEvent
import br.com.zup.axon.saga.event.WithdrawMoneyCommand
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test

class AccountTest {

    private lateinit var fixture: AggregateTestFixture<Account>
    private val tenant = "sample_segment"

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(Account::class.java)
    }

    private val metaData = mapOf("tenant" to tenant)

    @Test
    fun `create account`() {
        fixture.givenNoPriorActivity()
                .`when`(CreateAccountCommand("1", "Darth Vader", Gender.MALE, 100), metaData)
                .expectEvents(AccountCreatedEvent("1", "Darth Vader", Gender.MALE, 100, tenant))
    }

    @Test
    fun `deposit`() {
        fixture.given(AccountCreatedEvent("1", "Darth Vader", Gender.MALE, 100, tenant), metaData)
                .`when`(DepositMoneyCommand("1", "tx1", 100), metaData)
                .expectEvents(MoneyDepositedEvent("1", "tx1", 100, 200, tenant))
    }

    @Test
    fun `withdraw valid value`() {
        fixture.given(AccountCreatedEvent("1", "Darth Vader", Gender.MALE, 100, tenant), metaData)
                .`when`(WithdrawMoneyCommand("1", "tx1", 100), metaData)
                .expectEvents(MoneyWithdrawnEvent("1", "tx1", 100, 0))
    }

    @Test
    fun `withdraw invalid value`() {
        fixture.given(AccountCreatedEvent("1", "Darth Vader", Gender.MALE, 100, tenant), metaData)
                .`when`(WithdrawMoneyCommand("1", "tx1", 200), metaData)
                .expectEvents(MoneyWithdrawRejectedEvent("1", "tx1", 200, 100))
    }
}