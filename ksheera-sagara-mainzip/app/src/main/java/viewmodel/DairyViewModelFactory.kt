package com.example.ksheera_sagara.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ksheera_sagara.data.DairyDao

class DairyViewModelFactory(
    private val dao: DairyDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DairyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DairyViewModel(dao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
