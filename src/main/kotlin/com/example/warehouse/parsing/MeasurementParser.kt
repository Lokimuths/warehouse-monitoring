package com.example.warehouse.parsing

import com.example.warehouse.Humidity
import com.example.warehouse.Temperature

object MeasurementParser {

    fun parseTemperature(raw: String): Temperature? {
        val (id, v) = extract(raw) ?: return null
        return Temperature(id, v)
    }

    fun parseHumidity(raw: String): Humidity? {
        val (id, v) = extract(raw) ?: return null
        return Humidity(id, v)
    }

    private fun extract(raw: String): Pair<String, Double>? {
        var id: String? = null
        var value: Double? = null
        for (part in raw.split(";")) {
            val kv = part.split("=", limit = 2)
            if (kv.size != 2) continue
            when (kv[0].trim()) {
                "sensor_id" -> id = kv[1].trim()
                "value" -> value = kv[1].trim().toDoubleOrNull()
            }
        }
        return if (id != null && value != null) id to value else null
    }
}
