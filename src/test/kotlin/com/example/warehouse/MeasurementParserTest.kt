package com.example.warehouse

import com.example.warehouse.parsing.MeasurementParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MeasurementParserTest {

    @Test
    fun parsesTemp() {
        Assertions.assertEquals(Temperature("t1", 30.0), MeasurementParser.parseTemperature("sensor_id=t1; value=30"))
    }

    @Test
    fun parsesHumidity() {
        Assertions.assertEquals(Humidity("h1", 40.0), MeasurementParser.parseHumidity("sensor_id=h1; value=40"))
    }

    @Test
    fun nullOnBadNumber() {
        Assertions.assertNull(MeasurementParser.parseTemperature("sensor_id=t1; value=hot"))
    }
}