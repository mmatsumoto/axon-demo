package br.com.zup.axon.bank.domain.account.upcast

import br.com.zup.axon.bank.domain.account.MoneyDepositedEvent
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster

class MoneyDepositedEventUpcaster: SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(MoneyDepositedEvent::class.java.typeName, "1.0")

    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean =
            intermediateRepresentation.type == targetType

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation =
            intermediateRepresentation.upcastPayload(
                    SimpleSerializedType(targetType.name, "2.0"), JsonNode::class.java) { jsonNode ->

                if (jsonNode is ObjectNode) {
                    jsonNode.put("tenant", intermediateRepresentation.metaData.`object`["tenant"] as String)
                }

                jsonNode
            }

}
