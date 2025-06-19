package com.cduffaut.ft_hangouts
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyApplication : Application() {
    private var activeActivities = 0

    companion object {
        var isPhotoSelectionActive = false
        var isFirstStart = true
    }

    // monitor the life cycle of the app, when it's on the background and when you have to display the horodatage toast
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                if (activeActivities == 0) {
                    // if we just selected a picture : do not show the toast
                    if (isPhotoSelectionActive) {
                        isPhotoSelectionActive = false
                    }
                    // if we launched app for the 1st time : do not show the toast
                    else if (isFirstStart) {
                        isFirstStart = false
                    }
                    else {
                        val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
                        val backgroundTime = sharedPrefs.getLong("background_time", 0)
                        if (backgroundTime > 0) {
                            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val dateString = formatter.format(Date(backgroundTime))
                            try {
                                Toast.makeText(activity,
                                    activity.getString(R.string.background_time, dateString),
                                    Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(activity,
                                    "App in background since: $dateString",
                                    Toast.LENGTH_LONG).show()
                            }
                            // re-initialisation
                            sharedPrefs.edit().remove("background_time").apply()
                        }
                    }
                }
                activeActivities++
            }
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                activeActivities--
                if (activeActivities == 0) {
                    // app goes in background
                    val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putLong("background_time", System.currentTimeMillis()).apply()
                }
            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}