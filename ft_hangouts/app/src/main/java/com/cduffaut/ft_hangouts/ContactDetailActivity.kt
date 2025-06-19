package com.cduffaut.ft_hangouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cduffaut.ft_hangouts.database.DatabaseHelper
import com.cduffaut.ft_hangouts.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailBinding
    private lateinit var dbHelper: DatabaseHelper
    private var contactId: Long = -1

    // retrieving the contact ID from intent + loading the contact data
    // and configuring button listeners for editing the contact
    // messaging the contact and calling the contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        ThemeUtils.applyHeaderColor(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        contactId = intent.getLongExtra("CONTACT_ID", -1)
        if (contactId == -1L) {
            Toast.makeText(this, getString(R.string.contact_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadContactData()

        binding.buttonEdit.setOnClickListener {
            val intent = Intent(this, EditContactActivity::class.java).apply {
                putExtra("CONTACT_ID", contactId)
            }
            startActivity(intent)
        }

        binding.buttonMessage.setOnClickListener {
            val intent = Intent(this, MessagingActivity::class.java).apply {
                putExtra("CONTACT_ID", contactId)
            }
            startActivity(intent)
        }

        binding.buttonCall.setOnClickListener {
            callContact()
        }
    }

    // retrieves the contact information from the database using the contact ID
    // displays the contact's full name in the title bar and populates the UI
    // with all contact details including name, phone, email, address, and photo
    private fun loadContactData() {
        val contact = dbHelper.getContactById(contactId)
        if (contact != null) {
            supportActionBar?.title = "${contact.firstname} ${contact.name}"

            binding.contactName.text = contact.name
            binding.contactFirstName.text = contact.firstname
            binding.contactPhone.text = contact.phone
            binding.contactEmail.text = contact.email.ifEmpty { getString(R.string.not_specified) }
            binding.contactAddress.text = contact.address.ifEmpty { getString(R.string.not_specified) }

            binding.contactPhoto.setImageDrawable(null)

            if (contact.photo.isNotEmpty()) {
                try {
                    binding.contactPhoto.setImageURI(Uri.parse(contact.photo))
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.contactPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                binding.contactPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    // initiates a phone call to the contact
    // and the contact's phone number checking for proper permissions before starting the call.
    private fun callContact() {
        val contact = dbHelper.getContactById(contactId)
        if (contact != null) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${contact.phone}")

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.call_permission_required), Toast.LENGTH_SHORT).show()
                return
            }

            startActivity(intent)
        }
    }

    // reloads the contact data when the activity becomes visible again
    // to ensure the displayed information is current
    override fun onResume() {
        super.onResume()
        loadContactData()
    }

    // handles the navigation back button in the toolbar by returning to the previous screen when pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}