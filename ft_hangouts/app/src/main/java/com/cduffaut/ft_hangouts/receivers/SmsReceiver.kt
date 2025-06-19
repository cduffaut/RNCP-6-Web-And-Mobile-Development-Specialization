package com.cduffaut.ft_hangouts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import com.cduffaut.ft_hangouts.database.DatabaseHelper
import com.cduffaut.ft_hangouts.models.Contact
import com.cduffaut.ft_hangouts.models.Message

class SmsReceiver : BroadcastReceiver() {

    // captures incoming SMS events from the Android system, extracts the sender phone number
    // and message content from each received SMS then
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (sms in smsMessages) {
                val phoneNumber = sms.originatingAddress ?: continue
                val messageBody = sms.messageBody

                // processing received msg
                processReceivedSms(context, phoneNumber, messageBody)
            }
        }
    }

    // handles a received SMS: checking if the sender exists in contacts DB, creating
    // a new contact if the sender is not found and preparing to store the message
    // with the appropriate contact information
    private fun processReceivedSms(context: Context, phoneNumber: String, messageBody: String) {
        val dbHelper = DatabaseHelper(context)

        // check if nbr already exist
        var contact = dbHelper.getContactByPhone(phoneNumber)

        if (contact == null) {
            // create new contact with number as name
            contact = Contact(
                name = phoneNumber,
                phone = phoneNumber
            )
            val newContactId = dbHelper.insertContact(contact)
            if (newContactId > 0) {
                contact = contact.copy(id = newContactId)
                Toast.makeText(context, "Nouveau contact créé: $phoneNumber", Toast.LENGTH_SHORT).show()
            }
        }

        // save msg
        if (contact.id != -1L) {
            val message = Message(
                contactId = contact.id,
                message = messageBody,
                isSent = false
            )
            dbHelper.insertMessage(message)
        }
    }
}