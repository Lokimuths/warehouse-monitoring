package com.example.warehouse.messaging

import kotlinx.serialization.Serializable

@Serializable
enum class SensorType { TEMPERATURE, HUMIDITY }

@Serializable
data class MeasurementMessage(
    val warehouseId: String,
    val sensorType: SensorType,
    val sensorId: String,
    val value: Double,
)