package com.example.warehouse

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.javadsl.Behaviors
import com.example.warehouse.actors.CentralMonitoringActor
import com.example.warehouse.messaging.MeasurementConsumer
import com.example.warehouse.messaging.MeasurementPublisher
import com.example.warehouse.parsing.MeasurementParser
import com.example.warehouse.actors.UdpSensorListener
import com.example.warehouse.actors.WarehouseActor
import com.example.warehouse.messaging.SensorType

fun main() {
    ActorSystem.create(guardian(), "warehouse-monitoring")
}

private fun guardian(): Behavior<Void> = Behaviors.setup { ctx ->
    val config = ctx.system.settings().config()
    val warehouseId = config.getString("warehouse.id")
    val tempPort = config.getInt("monitoring.temperature.port")
    val tempThreshold = config.getDouble("monitoring.temperature.threshold")
    val humidityPort = config.getInt("monitoring.humidity.port")
    val humidityThreshold = config.getDouble("monitoring.humidity.threshold")
    val rabbitHost = System.getenv("RABBITMQ_HOST") ?: "localhost"

    val central = ctx.spawn(
        CentralMonitoringActor.create(tempThreshold, humidityThreshold),
        "central",
    )
    val consumer = MeasurementConsumer(
        host = rabbitHost,
        onMessage = { message ->
            val m = when (message.sensorType) {
                SensorType.TEMPERATURE -> Temperature(message.sensorId, message.value)
                SensorType.HUMIDITY -> Humidity(message.sensorId, message.value)
            }
            central.tell(CentralMonitoringActor.Record(message.warehouseId, m))
        },
    )
    consumer.start()

    val publisher = MeasurementPublisher(rabbitHost)
    val warehouse = ctx.spawn(
        WarehouseActor.create(warehouseId, publisher::publish),
        "warehouse",
    )
    ctx.spawn(
        UdpSensorListener.create(tempPort, MeasurementParser::parseTemperature, warehouse),
        "temperature-listener",
    )
    ctx.spawn(
        UdpSensorListener.create(humidityPort, MeasurementParser::parseHumidity, warehouse),
        "humidity-listener",
    )

    Behaviors.receive(Void::class.java)
        .onSignal(PostStop::class.java) { _ ->
            publisher.close()
            consumer.close()
            Behaviors.same()
        }
        .build()
}
