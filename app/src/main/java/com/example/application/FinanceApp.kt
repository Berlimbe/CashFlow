package com.example.application

import com.example.application.AppRepository
import android.app.Application


class FinanceApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    private val currencyApiService by lazy { RetrofitClient.instance }

    val repository by lazy {
        AppRepository(database.appDao(), currencyApiService)
    }
}