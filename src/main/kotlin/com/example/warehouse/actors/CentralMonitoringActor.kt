package com.example.warehouse.actors

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors
import com.example.warehouse.Humidity
import com.example.warehouse.Measurement
import com.example.warehouse.Temperature

object CentralMonitoringActor {

    sealed interface Command
    data class Record(val warehouseId: String, val measurement: Measurement) : Command

    fun create(tempThreshold: Double = 35.0, humidityThreshold: Double = 50.0): Behavior<Command> =
        Behaviors.setup { ctx ->
            Behaviors.receive(Command::class.java)
                .onMessage(Record::class.java) { msg ->
                    val m = msg.measurement
                    val (label, threshold) = when (m) {
                        is Temperature -> "temperature" to tempThreshold
                        is Humidity -> "humidity" to humidityThreshold
                    }
                    if (m.value > threshold) {
                        ctx.log.warn("ALARM | {}:{} {}={} exceeds {}",
                            msg.warehouseId, m.sensorId, label, m.value, threshold)
                    }
                    Behaviors.same()
                }
                .build()
        }
}