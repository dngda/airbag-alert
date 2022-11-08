package com.airmineral.airbagalert

import android.app.Application
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.data.repo.AlertRepository
import com.airmineral.airbagalert.data.repo.AuthRepository
import com.airmineral.airbagalert.ui.incident.IncidentViewModel
import com.airmineral.airbagalert.ui.auth.AuthViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            singleOf(::AlertRepository)
            singleOf(::AuthRepository)
            singleOf(::PreferenceProvider)

            viewModelOf(::AuthViewModel)
            viewModelOf(::IncidentViewModel)
        }

        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)
            modules(appModule)
        }
    }
}
