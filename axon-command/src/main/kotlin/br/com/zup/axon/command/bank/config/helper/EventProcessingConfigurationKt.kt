package br.com.zup.axon.command.bank.config.helper

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor


fun EventProcessingConfiguration.trackingEventProcessor(name: String, apply: (TrackingEventProcessor) -> Unit) {
    this.eventProcessor<TrackingEventProcessor>(name)
            .ifPresent(apply)
}
