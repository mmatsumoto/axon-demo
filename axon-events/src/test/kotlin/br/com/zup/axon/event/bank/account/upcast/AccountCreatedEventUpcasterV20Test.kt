package br.com.zup.axon.event.bank.account.upcast


import br.com.zup.axon.event.bank.account.AccountCreatedEvent
import br.com.zup.axon.event.bank.account.Gender
import com.fasterxml.jackson.databind.node.ObjectNode
import org.axonframework.eventsourcing.eventstore.GenericDomainEventEntry
import org.axonframework.serialization.SimpleSerializedObject
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation
import org.axonframework.serialization.upcasting.event.UpcastedEventRepresentation
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountCreatedEventUpcasterV20Test {

    private val serializer = JacksonSerializer()
    private val objectMapper = serializer.objectMapper

    private val upcasterV20 = AccountCreatedEventUpcasterV20()
    private val upcasterV30 = AccountCreatedEventUpcasterV30()

    @Language("JSON")
    val payload = "{\"id\":\"account-id-1\",\"name\":\"Name 1\",\"balance\":10000}"

    @Language("JSON")
    val metaData = "{\"tenant\": \"sample_segment\"}"

    @Before
    fun setUp() {}

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `upcast v1 to v2 to v3`() {

        val intermediateRepresentationV10 = InitialEventRepresentation(GenericDomainEventEntry("Account",
                                                                                               "account-id-1",
                                                                                               0,
                                                                                               "1",
                                                                                               Instant.now(),
                                                                                               "br.com.zup.axon.event.bank.account.AccountCreatedEvent",
                                                                                               "1.0",
                                                                                               payload,
                                                                                               metaData), serializer)

        assertTrue("payloadRevision is not equals to '1.0'") { upcasterV20.canUpcast(intermediateRepresentationV10) }

        val upcastedEventV20 = upcasterV20.doUpcast(intermediateRepresentationV10) as UpcastedEventRepresentation<*>

        val dataRevision20 = upcastedEventV20.data as SimpleSerializedObject<ObjectNode>

        assertEquals("br.com.zup.axon.event.bank.account.AccountCreatedEvent", dataRevision20.type.name)
        assertEquals("2.0", dataRevision20.type.revision)

        assertEquals("account-id-1", dataRevision20.data.get("id").asText())
        assertEquals("Name 1", dataRevision20.data.get("name").asText())
        assertEquals(10000L, dataRevision20.data.get("balance").asLong())
        assertEquals("sample_segment", dataRevision20.data.get("tenant").asText())




        val intermediateRepresentationV20 = InitialEventRepresentation(GenericDomainEventEntry("Account",
                                                                                               "account-id-1",
                                                                                               0,
                                                                                               "1",
                                                                                               Instant.now(),
                                                                                               "br.com.zup.axon.event.bank.account.AccountCreatedEvent",
                                                                                               "2.0",
                                                                                               dataRevision20.data.toString(),
                                                                                               metaData), serializer)

        assertTrue("payloadRevision is not equals to '2.0'") { upcasterV30.canUpcast(intermediateRepresentationV20) }


        val upcastedEventV30 = upcasterV30.doUpcast(intermediateRepresentationV20) as UpcastedEventRepresentation<*>

        val dataRevision30 = upcastedEventV30.data as SimpleSerializedObject<ObjectNode>

        assertEquals("br.com.zup.axon.event.bank.account.AccountCreatedEvent", dataRevision30.type.name)
        assertEquals("3.0", dataRevision30.type.revision)

        val accountRevision30 = objectMapper.convertValue(dataRevision30.data, AccountCreatedEvent::class.java)

        assertEquals(AccountCreatedEvent("account-id-1",
                                         "Name 1",
                                         Gender.UNKNOWN,
                                         10000L,
                                         "sample_segment"), accountRevision30)


    }


}
