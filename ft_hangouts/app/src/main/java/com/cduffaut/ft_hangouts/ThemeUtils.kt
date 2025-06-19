package com.cduffaut.ft_hangouts

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

// Header parameters: color, white text...
object ThemeUtils {
    fun applyHeaderColor(activity: AppCompatActivity) {
        val prefs = activity.getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        val headerColor = prefs.getInt("header_color", activity.resources.getColor(R.color.black, activity.theme))

        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar?.setBackgroundColor(headerColor)

        toolbar?.setTitleTextColor(activity.resources.getColor(android.R.color.white, activity.theme))

        activity.window.statusBarColor = headerColor
    }
}