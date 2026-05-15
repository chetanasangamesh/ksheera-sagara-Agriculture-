package com.example.ksheera_sagara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ksheera_sagara.data.DairyDatabase
import com.example.ksheera_sagara.ui.KsheeraSagaraApp
import com.example.ksheera_sagara.viewmodel.DairyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DairyDatabase.getDatabase(this)
        val dairyViewModel = DairyViewModel(database.dairyDao())

        setContent {
            KsheeraSagaraApp(dairyViewModel)
        }
    }
}
