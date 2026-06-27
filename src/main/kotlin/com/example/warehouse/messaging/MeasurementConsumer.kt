package com.example.warehouse.messaging

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.DeliverCallback
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class MeasurementConsumer(
    host: String,
    private val onMessage: (MeasurementMessage) -> Unit,
    private val exchange: String = "measurements",
    private val queue: String = "central.measurements",
) {
    private val connection = BrokerConnection.open(host)
    private val channel = connection.createChannel()
    private val log = LoggerFactory.getLogger(MeasurementConsumer::class.java)

    fun start() {
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT,  true)
        channel.queueDeclare(queue, true, false, false, null)
        channel.queueBind(queue, exchange, "")

        val onDeliver = DeliverCallback { _, delivery ->
            runCatching {
                val message = Json.decodeFromString<MeasurementMessage>(String(delivery.body))
                onMessage(message)
            }.onFailure { log.warn("Failed to handle delivery: {}", String(delivery.body), it) }
        }
        channel.basicConsume(queue, true, onDeliver) { }
    }

    fun close() {
        runCatching { channel.close() }
        runCatching { connection.close() }
    }
}