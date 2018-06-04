package br.com.zup.axon.saga.event.upcast

import br.com.zup.axon.saga.aggregate.Account
import br.com.zup.axon.saga.aggregate.Gender
import br.com.zup.axon.saga.event.AccountCreatedEvent
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster

class AccountCreatedEventUpcasterV20: SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(AccountCreatedEvent::class.java.typeName, null)
    // revision was null at first version


    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean =
            intermediateRepresentation.type == targetType

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {

        return intermediateRepresentation.upcastPayload(
                SimpleSerializedType(targetType.name, "2.0"), JsonNode::class.java) { jsonNode ->

            if (jsonNode is ObjectNode) {
                jsonNode.put("tenant", intermediateRepresentation.metaData.`object`["tenant"] as String)
            }

            jsonNode
        }

    }

}


class AccountCreatedEventUpcasterV30: SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(AccountCreatedEvent::class.java.typeName, "2.0")


    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == targetType
    }

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {

        return intermediateRepresentation.upcastPayload(
                SimpleSerializedType(targetType.name, "3.0"), JsonNode::class.java) { jsonNode ->

            if (jsonNode is ObjectNode) {
                jsonNode.put("gender", Gender.UNKNOWN.name)
            }

            jsonNode
        }

    }

}


class AccountSnapshotUpcasterV20: SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(Account::class.java.typeName, "2.0")
    // revision was null at first version


    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean =
            intermediateRepresentation.type == targetType

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {

        return intermediateRepresentation.upcastPayload(
                SimpleSerializedType(targetType.name, "3.0"), JsonNode::class.java) { jsonNode ->

            if (jsonNode is ObjectNode) {
                jsonNode.put("lastName",
                             (jsonNode.get("name").toString()).substringAfter(" "))
            }

            jsonNode
        }

    }

}
