package com.example.warehouse.messaging

import com.rabbitmq.client.BuiltinExchangeType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MeasurementPublisher(
    host: String,
    private val exchange: String = "measurements",
) {
    private val connection = BrokerConnection.open(host)
    private val channel = connection.createChannel().apply {
        exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true)
    }

    fun publish(message: MeasurementMessage) {
        val body = Json.encodeToString(message).toByteArray()
        channel.basicPublish(exchange, "", null, body)
    }

    fun close() {
        runCatching { channel.close() }
        runCatching { connection.close() }
    }
}