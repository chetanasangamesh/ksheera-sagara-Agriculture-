package com.example.ksheera_sagara.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksheera_sagara.data.DairyDao
import com.example.ksheera_sagara.data.Expense
import com.example.ksheera_sagara.data.MilkEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netProfit: Double = 0.0
)

class DairyViewModel(
    private val dao: DairyDao
) : ViewModel() {

    val milkEntries = dao.getMilkEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val expenses = dao.getExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMilkEntry(
        liters: Double,
        fat: Double,
        snf: Double,
        ratePerLiter: Double
    ) {
        viewModelScope.launch {
            val entry = MilkEntry(
                date = System.currentTimeMillis(),
                liters = liters,
                fat = fat,
                snf = snf,
                ratePerLiter = ratePerLiter,
                totalAmount = liters * ratePerLiter
            )
            dao.insertMilkEntry(entry)
        }
    }

    fun addExpense(
        category: String,
        amount: Double,
        note: String = ""
    ) {
        viewModelScope.launch {
            val expense = Expense(
                date = System.currentTimeMillis(),
                category = category,
                amount = amount,
                note = note
            )
            dao.insertExpense(expense)
        }
    }

    fun deleteMilkEntry(id: Int) {
        viewModelScope.launch {
            dao.deleteMilkEntryById(id)
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            dao.deleteExpenseById(id)
        }
    }

    fun calculateSummary(
        milkEntries: List<MilkEntry>,
        expenses: List<Expense>
    ): FinancialSummary {
        val totalIncome = milkEntries.sumOf { it.totalAmount }
        val totalExpense = expenses.sumOf { it.amount }

        return FinancialSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netProfit = totalIncome - totalExpense
        )
    }

    fun clearAllData() {
        viewModelScope.launch {
            dao.deleteAllMilkEntries()
            dao.deleteAllExpenses()
        }
    }
}
