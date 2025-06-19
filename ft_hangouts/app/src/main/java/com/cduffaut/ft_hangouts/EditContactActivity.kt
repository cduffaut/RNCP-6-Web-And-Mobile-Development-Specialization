package com.cduffaut.ft_hangouts
import android.app.Activity
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cduffaut.ft_hangouts.database.DatabaseHelper
import com.cduffaut.ft_hangouts.databinding.ActivityEditContactBinding
import com.cduffaut.ft_hangouts.models.Contact

class EditContactActivity : AppCompatActivity() {
    private var selectedPhotoUri: String = ""
    private lateinit var binding: ActivityEditContactBinding
    private lateinit var dbHelper: DatabaseHelper
    private var contactId: Long = -1
    private var photoUri: String = ""

    // initializes the contact editing activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ThemeUtils.applyHeaderColor(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dbHelper = DatabaseHelper(this)

        // check if we're editing an existing contact
        contactId = intent.getLongExtra("CONTACT_ID", -1)
        if (contactId != -1L) {
            // edition mode
            supportActionBar?.title = getString(R.string.edit_contact)
            loadContactData()
            binding.buttonDelete.visibility = android.view.View.VISIBLE
        } else {
            // creation mode
            supportActionBar?.title = getString(R.string.add_contact)
            binding.buttonDelete.visibility = android.view.View.GONE
        }

        binding.buttonChoosePhoto.setOnClickListener {
            choosePhotoFromGallery()
        }

        binding.buttonSave.setOnClickListener {
            saveContact()
        }

        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun loadContactData() {
        val contact = dbHelper.getContactById(contactId)
        if (contact != null) {
            binding.editName.setText(contact.name)
            binding.editFirstName.setText(contact.firstname)
            binding.editPhone.setText(contact.phone)
            binding.editEmail.setText(contact.email)
            binding.editAddress.setText(contact.address)
            photoUri = contact.photo
            if (photoUri.isNotEmpty()) {
                try {
                    binding.contactPhoto.setImageURI(Uri.parse(photoUri))
                } catch (e: Exception) {
                // if img could not be set -> we do nothing
                }
            }
        }
    }

    private fun saveContact() {
        val name = binding.editName.text.toString().trim()
        val firstname = binding.editFirstName.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.required_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val finalPhotoUri = if (selectedPhotoUri.isNotEmpty()) selectedPhotoUri else photoUri

        val contact = Contact(
            id = contactId,
            name = name,
            firstname = firstname,
            phone = phone,
            email = binding.editEmail.text.toString().trim(),
            address = binding.editAddress.text.toString().trim(),
            photo = finalPhotoUri
        )

        if (contactId == -1L) {
            // Nouveau contact
            val newId = dbHelper.insertContact(contact)
            if (newId > 0) {
                Toast.makeText(this, getString(R.string.contact_added), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_adding_contact), Toast.LENGTH_SHORT).show()
            }
        } else {
            // Mise à jour
            val rowsAffected = dbHelper.updateContact(contact)
            if (rowsAffected > 0) {
                Toast.makeText(this, getString(R.string.contact_updated), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_updating_contact), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // displays an alert dialog asking the user to confirm deletion of a contact
    // with options to proceed with deletion or cancel the operation
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteContact()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    // removes the contact from the database if a valid contact ID exists
    // shows a success or failure message based on the operation's result
    // and closes the activity if deletion was successful
    private fun deleteContact() {
        if (contactId != -1L) {
            val result = dbHelper.deleteContact(contactId)
            if (result > 0) {
                Toast.makeText(this, getString(R.string.contact_deleted), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_deleting_contact), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // indicating photo selection is active, marking navigation as internal
    // and launching the system's image to allow the user to select a contact photo
    private fun saveImageToAppStorage(sourceUri: Uri): String {
        try {
            val timeStamp = System.currentTimeMillis()
            val fileName = "contact_photo_$timeStamp.jpg"
            val destinationFile = File(filesDir, fileName)

            // copy img
            contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            // create a new URI
            val fileUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".fileprovider",
                destinationFile
            )

            // string format URI returned
            return fileUri.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
            // if any problem empty string is returned
            return ""
        }
    }

    // indicating photo selection is active and marking navigation as internal
    // + launching the system's image picker to allow the user to select a contact photo.
    private fun choosePhotoFromGallery() {
        MyApplication.isPhotoSelectionActive = true

        val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("is_navigating_within_app", true).apply()

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // saving the selected image to app storage and updating the
    // contact photo UI when the user returns from the image picker activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("is_navigating_within_app", true).apply()

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            try {
                val selectedImage = data.data!!

                // Stocker l'URI dans la nouvelle variable ET dans photoUri
                selectedPhotoUri = saveImageToAppStorage(selectedImage)
                photoUri = selectedPhotoUri

                if (photoUri.isNotEmpty()) {
                    binding.contactPhoto.setImageURI(Uri.parse(photoUri))
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // stores information that the user is navigating within the application
    // when the activity is no longer in the foreground
    override fun onPause() {
        super.onPause()

        val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("is_navigating_within_app", true).apply()
    }

    // applies the theme color and reloads the contact data when the activity becomes visible again
    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
        if (contactId != -1L) {
            loadContactData()
        }
    }

    // indicates that the user remains within the application and then handles
    // returning to the previous screen when the navigation button is pressed
    override fun onSupportNavigateUp(): Boolean {
        val sharedPrefs = getSharedPreferences("ft_hangouts_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("is_navigating_within_app", true).apply()
        onBackPressed()
        return true
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}