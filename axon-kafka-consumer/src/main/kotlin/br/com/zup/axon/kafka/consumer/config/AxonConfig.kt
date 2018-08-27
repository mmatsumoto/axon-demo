package br.com.zup.axon.kafka.consumer.config

import org.axonframework.kafka.eventhandling.DefaultKafkaMessageConverter
import org.axonframework.kafka.eventhandling.KafkaMessageConverter
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class AxonConfig {

    @Primary
    @Bean("serializer")
    fun serializer(): Serializer = JacksonSerializer()

    @ConditionalOnMissingBean
    @Bean
    fun kafkaMessageConverter(
            @Qualifier("eventSerializer") eventSerializer: Serializer): KafkaMessageConverter<String, ByteArray> {
        return DefaultKafkaMessageConverter(eventSerializer)
    }
}