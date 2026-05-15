package com.example.ksheera_sagara.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DairyDao {

    @Insert
    suspend fun insertMilkEntry(entry: MilkEntry)

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM milk_entries ORDER BY date DESC")
    fun getMilkEntries(): Flow<List<MilkEntry>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getExpenses(): Flow<List<Expense>>

    @Query("DELETE FROM milk_entries")
    suspend fun deleteAllMilkEntries()

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Query("DELETE FROM milk_entries WHERE id = :id")
    suspend fun deleteMilkEntryById(id: Int)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Int)
}
