package com.ke.dawaati.app

import android.app.Application
import com.ke.dawaati.preference.configurationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DawatiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DawatiApp)
            modules(
                dawatiAPIModule,
                configurationModule,
                dawatiModule
            )
        }
    }
}
