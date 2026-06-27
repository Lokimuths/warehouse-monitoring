package com.example.warehouse

import akka.actor.testkit.typed.javadsl.ActorTestKit
import com.example.warehouse.actors.WarehouseActor
import com.example.warehouse.messaging.MeasurementMessage
import com.example.warehouse.messaging.SensorType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList

class WarehouseActorTest {

    companion object {
        private val testKit = ActorTestKit.create()
        @JvmStatic @AfterAll
        fun cleanup() = testKit.shutdownTestKit()
    }

    @Test
    fun tagsReadingWithWarehouseIdAndPublishesIt() {
        val published = CopyOnWriteArrayList<MeasurementMessage>()
        val warehouse = testKit.spawn(WarehouseActor.create("warehouse-1", publish = { published.add(it) }))
        val probe = testKit.createTestProbe<Any>()

        warehouse.tell(WarehouseActor.Reading(Temperature("t1", 30.0)))

        probe.awaitAssert {
            Assertions.assertEquals(1, published.size)
            Assertions.assertEquals("warehouse-1", published.first().warehouseId)
            Assertions.assertEquals(SensorType.TEMPERATURE, published.first().sensorType)
            null
        }
    }
}