package com.cduffaut.swiftycompanion

import android.app.Application

// point 0 de l'app
// init tiken manager (main var du projet)
class SwiftyCompanionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}