package com.cduffaut.ft_hangouts

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cduffaut.ft_hangouts.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences
    private var currentColor = 0

    // initialise parameters for header
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        ThemeUtils.applyHeaderColor(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        prefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        currentColor = prefs.getInt("header_color", resources.getColor(R.color.colorPrimary, theme))

        updateToolbarColor()

        binding.colorOption1.setOnClickListener { changeHeaderColor(R.color.colorPrimary) }
        binding.colorOption2.setOnClickListener { changeHeaderColor(R.color.colorRed) }
        binding.colorOption3.setOnClickListener { changeHeaderColor(R.color.colorBlue) }
        binding.colorOption4.setOnClickListener { changeHeaderColor(R.color.colorGreen) }
        binding.colorOption5.setOnClickListener { changeHeaderColor(R.color.colorPurple) }
    }

    private fun changeHeaderColor(colorResId: Int) {
        currentColor = resources.getColor(colorResId, theme)
        prefs.edit().putInt("header_color", currentColor).apply()
        updateToolbarColor()
    }

    private fun updateToolbarColor() {
        binding.toolbar.setBackgroundColor(currentColor)
        window.statusBarColor = currentColor
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}