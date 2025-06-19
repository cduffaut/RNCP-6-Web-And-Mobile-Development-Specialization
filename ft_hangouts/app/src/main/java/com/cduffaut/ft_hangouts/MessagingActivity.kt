package com.cduffaut.ft_hangouts

import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cduffaut.ft_hangouts.adapters.MessageAdapter
import com.cduffaut.ft_hangouts.database.DatabaseHelper
import com.cduffaut.ft_hangouts.databinding.ActivityMessagingBinding
import com.cduffaut.ft_hangouts.models.Message

class MessagingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagingBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var messageAdapter: MessageAdapter
    private var contactId: Long = -1
    private lateinit var contactPhone: String
    private lateinit var contactName: String

    // set up user interface with contacts infos...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagingBinding.inflate(layoutInflater)
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

        loadContactInfo()
        setupRecyclerView()
        loadMessages()

        binding.buttonSend.setOnClickListener {
            sendMessage()
        }
    }

    // get data from contact infos
    private fun loadContactInfo() {
        val contact = dbHelper.getContactById(contactId)
        if (contact != null) {
            contactPhone = contact.phone
            contactName = "${contact.firstname} ${contact.name}"
            supportActionBar?.title = getString(R.string.messages_with_contact, contactName)
        } else {
            finish()
        }
    }

    // display the msg from bottom to top
    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessagingActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    // get back all msg from database related to an exchange
    private fun loadMessages() {
        val messages = dbHelper.getMessagesForContact(contactId)
        messageAdapter.submitList(messages)

        if (messages.isNotEmpty()) {
            binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
        }

        binding.emptyView.visibility = if (messages.isEmpty()) View.VISIBLE else View.GONE
    }

    // send an SMS and save it in DB
    private fun sendMessage() {
        val messageText = binding.editMessage.text.toString().trim()
        if (messageText.isEmpty()) {
            return
        }

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(contactPhone, null, messageText, null, null)

            // save the msg in the database
            val message = Message(
                contactId = contactId,
                message = messageText,
                isSent = true
            )
            dbHelper.insertMessage(message)

            binding.editMessage.text.clear()

            loadMessages()

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_sending_message, e.message), Toast.LENGTH_SHORT).show()
        }
    }

    // allow us to get back on the last page/screen (<-)
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}