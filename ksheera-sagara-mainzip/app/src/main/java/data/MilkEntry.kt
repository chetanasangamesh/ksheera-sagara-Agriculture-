package com.example.ksheera_sagara.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val liters: Double,
    val fat: Double,
    val snf: Double,
    val ratePerLiter: Double,
    val totalAmount: Double
)
