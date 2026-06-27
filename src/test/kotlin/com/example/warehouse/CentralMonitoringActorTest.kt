package com.example.warehouse

import akka.actor.testkit.typed.javadsl.ActorTestKit
import akka.actor.testkit.typed.javadsl.LoggingTestKit
import com.example.warehouse.actors.CentralMonitoringActor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

class CentralMonitoringActorTest {

    companion object {
        private val testKit = ActorTestKit.create()
        @JvmStatic @AfterAll
        fun cleanup() = testKit.shutdownTestKit()
    }

    @Test
    fun alarmFiresWhenAboveThreshold() {
        val central = testKit.spawn(CentralMonitoringActor.create(tempThreshold = 35.0))
        LoggingTestKit.warn("ALARM").expect(testKit.system()) {
            central.tell(CentralMonitoringActor.Record("w1", Temperature("t1", 40.0)))
            null
        }
    }

    @Test
    fun alarmFiresOnEveryBreachNotJustFirst() {
        val central = testKit.spawn(CentralMonitoringActor.create(tempThreshold = 35.0))
        LoggingTestKit.warn("ALARM").withOccurrences(2).expect(testKit.system()) {
            central.tell(CentralMonitoringActor.Record("w1", Temperature("t1", 40.0)))
            central.tell(CentralMonitoringActor.Record("w1", Temperature("t1", 41.0)))
            null
        }
    }
}