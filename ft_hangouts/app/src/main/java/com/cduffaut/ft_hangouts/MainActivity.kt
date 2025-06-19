package com.cduffaut.ft_hangouts
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cduffaut.ft_hangouts.adapters.ContactAdapter
import com.cduffaut.ft_hangouts.database.DatabaseHelper
import com.cduffaut.ft_hangouts.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import android.content.Context
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactAdapter: ContactAdapter
    private var isNavigatingWithinApp = false
    private var backgroundTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ThemeUtils.applyHeaderColor(this)
        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
        binding.fabAddContact.setOnClickListener {
            // tell that we're navigating within the app
            isNavigatingWithinApp = true
            // tell if we're editing contact
            val intent = Intent(this, EditContactActivity::class.java)
            startActivity(intent)
        }
        // ask necessary permissions
        requestPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // tell that we're navigating within the app
                isNavigatingWithinApp = true
                // tell if we're editing contact
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        // get time only if app is int the background
        if (!isChangingConfigurations && !isNavigatingWithinApp) {
            val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putLong("background_time", System.currentTimeMillis()).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
        // reload contacts
        loadContacts()
    }

    override fun onSupportNavigateUp(): Boolean {
        // tell that we're navigating within the app before going back on past window (<-)
        val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("is_navigating_within_app", true).apply()
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter { contact ->
            isNavigatingWithinApp = true
            // go to ContactDetailActivity
            val intent = Intent(this, ContactDetailActivity::class.java).apply {
                putExtra("CONTACT_ID", contact.id)
            }
            startActivity(intent)
        }
        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
        }
    }

    private fun loadContacts() {
        val contacts = dbHelper.getAllContacts()
        contactAdapter.submitList(contacts)
        // display the "empty" view if no contacts
        binding.emptyView.visibility = if (contacts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
    }
}