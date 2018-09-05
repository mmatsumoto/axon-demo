package br.com.zup.axon.event.bank.account.upcast

import br.com.zup.axon.event.bank.account.AccountCreatedEvent
import br.com.zup.axon.event.bank.account.Gender
import com.fasterxml.jackson.databind.node.ObjectNode
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster

class AccountCreatedEventUpcasterV20 : SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(AccountCreatedEvent::class.java.typeName, "1.0")

    public override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean =
            intermediateRepresentation.type == targetType

    public override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation =
            intermediateRepresentation.upcastPayload(
                    SimpleSerializedType(targetType.name, "2.0"), ObjectNode::class.java) { jsonNode ->

                jsonNode.put("tenant", intermediateRepresentation.metaData.`object`["tenant"] as String)

                jsonNode
            }


}


class AccountCreatedEventUpcasterV30 : SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(AccountCreatedEvent::class.java.typeName, "2.0")


    public override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean =
            intermediateRepresentation.type == targetType

    public override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {

        return intermediateRepresentation.upcastPayload(
                SimpleSerializedType(targetType.name, "3.0"), ObjectNode::class.java) { jsonNode ->

            jsonNode.put("gender", Gender.UNKNOWN.name)

            jsonNode
        }

    }

}

