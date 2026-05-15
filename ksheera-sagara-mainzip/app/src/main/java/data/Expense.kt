package com.example.ksheera_sagara.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val category: String,
    val amount: Double,
    val note: String = ""
)
