package com.example.warehouse.actors

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors
import com.example.warehouse.Humidity
import com.example.warehouse.Measurement
import com.example.warehouse.Temperature
import com.example.warehouse.messaging.MeasurementMessage
import com.example.warehouse.messaging.SensorType

object WarehouseActor {
    sealed interface Command
    data class Reading(val measurement: Measurement) : Command

    fun create(
        warehouseId: String,
        publish: (MeasurementMessage) -> Unit,
    ): Behavior<Command> = Behaviors.setup { ctx ->
        Behaviors.receive(Command::class.java)
            .onMessage(Reading::class.java) { msg ->
                val out = when (val m = msg.measurement) {
                    is Temperature -> MeasurementMessage(warehouseId, SensorType.TEMPERATURE, m.sensorId, m.value)
                    is Humidity -> MeasurementMessage(warehouseId, SensorType.HUMIDITY, m.sensorId, m.value)
                }
                ctx.log.debug("Publishing {}", out)
                publish(out)
                Behaviors.same()
            }
            .build()
    }
}