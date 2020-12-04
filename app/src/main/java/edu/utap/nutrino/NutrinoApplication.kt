package edu.utap.nutrino

import android.app.Application
import android.content.Context

class NutrinoApplication: Application() {
    companion object {
        lateinit var nutrinoApp: NutrinoApplication
        fun getInstance(): NutrinoApplication {
            return nutrinoApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        nutrinoApp = this
    }
}