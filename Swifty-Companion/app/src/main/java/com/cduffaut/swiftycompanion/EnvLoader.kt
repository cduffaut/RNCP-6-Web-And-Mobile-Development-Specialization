package com.cduffaut.swiftycompanion

import android.content.Context
import java.io.File
import java.util.Properties

object EnvLoader {
    private val properties = Properties()
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            try {
                // recupère nos var de connexion pour API via .env
                val envFile = File(context.applicationContext.filesDir.parentFile?.parentFile, ".env")
                if (envFile.exists()) {
                    properties.load(envFile.inputStream())
                    isInitialized = true
                    android.util.Log.d("EnvLoader", "Environment variables loaded from .env")
                }
                // si .env n'existe pas, on essaye local.properties
                else {
                    val localPropertiesFile = File(context.applicationContext.filesDir.parentFile?.parentFile, "local.properties")
                    if (localPropertiesFile.exists()) {
                        properties.load(localPropertiesFile.inputStream())
                        isInitialized = true
                        android.util.Log.d("EnvLoader", "Environment variables loaded from local.properties")
                    } else {
                        android.util.Log.e("EnvLoader", "Neither .env nor local.properties found")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("EnvLoader", "Error loading properties: ${e.message}")
            }
        }
    }
    fun get(key: String, defaultValue: String = ""): String {
        return properties.getProperty(key) ?: defaultValue
    }
}