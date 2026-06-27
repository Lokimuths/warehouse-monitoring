package com.example.warehouse.messaging

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object BrokerConnection {
    fun open(host: String): Connection {
        val factory = ConnectionFactory().apply { setHost(host) }
        repeat(10) {
            try { return factory.newConnection() } catch (_: Exception) { Thread.sleep(1000) }
        }
        return factory.newConnection()
    }
}