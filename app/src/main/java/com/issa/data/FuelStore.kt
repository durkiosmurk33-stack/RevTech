package com.issa.data
 // ✅ remove the "ui." part

data class FuelLog(
    val liters: Double,
    val cost: Double,
    val note: String,
    val date: String
)

object FuelStore {
    private val logs = mutableListOf<FuelLog>()

    fun add(log: FuelLog) {
        logs.add(log)
    }

    fun getAll(): List<FuelLog> {
        return logs.toList()
    }
}