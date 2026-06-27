package com.example.warehouse

sealed interface Measurement {
    val sensorId: String
    val value: Double
}

data class Temperature(override val sensorId: String, override val value: Double) : Measurement

data class Humidity(override val sensorId: String, override val value: Double) : Measurement