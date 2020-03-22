package com.syriatel.d3m.greenmile


import com.syriatel.d3m.greenmile.domain.ActionType
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@SpringBootApplication
class GreenMile

fun main(args: Array<String>) {
    runApplication<GreenMile>(*args)
}


@Configuration
@EnableConfigurationProperties(KafkaProperties::class)
class StreamsApp {


    @Bean(name = ["greenMileTopology"])
    fun topology() = StreamsBuilder().apply {
        ActionType.values().map {
            stream<String, String>(it.topic).mapValues { _, v ->
                it.toAction(v.split(",").toTypedArray())
            }
        }.reduce { acc, kStream ->
            acc.merge(kStream)
        }
    }

    @Bean
    fun kafkaStreams(streamsBuilder: StreamsBuilder, properties: KafkaProperties) =
            KafkaStreams(streamsBuilder.build(), Properties().apply {
                putAll(properties.buildStreamsProperties())
            }).apply {
                start()
            }
}
