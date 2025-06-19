package com.cduffaut.ft_hangouts.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cduffaut.ft_hangouts.models.Contact
import com.cduffaut.ft_hangouts.models.Message

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 1
    }

    // creates the initial database schema by executing SQL statements to define tables
    // for contacts and msg, including column specifications, keys...
    override fun onCreate(db: SQLiteDatabase) {
        // contacts table
        db.execSQL("""
            CREATE TABLE ${ContactContract.ContactEntry.TABLE_NAME} (
                ${ContactContract.ContactEntry.COLUMN_ID} INTEGER PRIMARY KEY,
                ${ContactContract.ContactEntry.COLUMN_NAME} TEXT NOT NULL,
                ${ContactContract.ContactEntry.COLUMN_FIRSTNAME} TEXT NOT NULL,
                ${ContactContract.ContactEntry.COLUMN_PHONE} TEXT NOT NULL,
                ${ContactContract.ContactEntry.COLUMN_EMAIL} TEXT,
                ${ContactContract.ContactEntry.COLUMN_ADDRESS} TEXT,
                ${ContactContract.ContactEntry.COLUMN_PHOTO} TEXT
            )
        """)

        // messages table
        db.execSQL("""
            CREATE TABLE ${MessageContract.MessageEntry.TABLE_NAME} (
                ${MessageContract.MessageEntry.COLUMN_ID} INTEGER PRIMARY KEY,
                ${MessageContract.MessageEntry.COLUMN_CONTACT_ID} INTEGER,
                ${MessageContract.MessageEntry.COLUMN_MESSAGE} TEXT NOT NULL,
                ${MessageContract.MessageEntry.COLUMN_TIMESTAMP} INTEGER,
                ${MessageContract.MessageEntry.COLUMN_IS_SENT} INTEGER,
                FOREIGN KEY(${MessageContract.MessageEntry.COLUMN_CONTACT_ID}) 
                REFERENCES ${ContactContract.ContactEntry.TABLE_NAME}(${ContactContract.ContactEntry.COLUMN_ID})
            )
        """)
    }

    // handles database version changes by dropping existing tables and recreating
    // them when the application is updated with a new database version
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${MessageContract.MessageEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ContactContract.ContactEntry.TABLE_NAME}")
        onCreate(db)
    }

    // adds a new contact to the database returning the new row ID
    fun insertContact(contact: Contact): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_FIRSTNAME, contact.firstname)
            put(ContactContract.ContactEntry.COLUMN_PHONE, contact.phone)
            put(ContactContract.ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactContract.ContactEntry.COLUMN_ADDRESS, contact.address)
            put(ContactContract.ContactEntry.COLUMN_PHOTO, contact.photo)
        }
        return db.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
    }

    // modifies an existing contact in the database by updating all fields
    // with new values from the Contact object : returns the number of rows affected
    fun updateContact(contact: Contact): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_FIRSTNAME, contact.firstname)
            put(ContactContract.ContactEntry.COLUMN_PHONE, contact.phone)
            put(ContactContract.ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactContract.ContactEntry.COLUMN_ADDRESS, contact.address)
            put(ContactContract.ContactEntry.COLUMN_PHOTO, contact.photo)
        }
        return db.update(
            ContactContract.ContactEntry.TABLE_NAME,
            values,
            "${ContactContract.ContactEntry.COLUMN_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    // removes a contact from the database by first deleting all associated
    // msg and then deleting the contact record, returning the number of rows affected
    fun deleteContact(contactId: Long): Int {
        val db = writableDatabase
        // suppressing associated msg
        db.delete(
            MessageContract.MessageEntry.TABLE_NAME,
            "${MessageContract.MessageEntry.COLUMN_CONTACT_ID} = ?",
            arrayOf(contactId.toString())
        )
        // deleting the contact
        return db.delete(
            ContactContract.ContactEntry.TABLE_NAME,
            "${ContactContract.ContactEntry.COLUMN_ID} = ?",
            arrayOf(contactId.toString())
        )
    }

    // retrieves a single contact from the database by its ID
    // converting the database cursor results into a Contact object
    fun getContactById(contactId: Long): Contact? {
        val db = readableDatabase
        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,
            null,
            "${ContactContract.ContactEntry.COLUMN_ID} = ?",
            arrayOf(contactId.toString()),
            null,
            null,
            null
        )

        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            contact = Contact(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME)),
                firstname = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_FIRSTNAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_EMAIL)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ADDRESS)),
                photo = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHOTO))
            )
        }
        cursor.close()
        return contact
    }

    // fetches all contacts from the database in alphabetical order by name
    // returning them as a list of Contact objects
    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = readableDatabase
        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${ContactContract.ContactEntry.COLUMN_NAME} ASC"
        )

        while (cursor.moveToNext()) {
            val contact = Contact(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME)),
                firstname = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_FIRSTNAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_EMAIL)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ADDRESS)),
                photo = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHOTO))
            )
            contacts.add(contact)
        }
        cursor.close()
        return contacts
    }

    // searches for a contact with a specific phone number
    // returning the matching Contact object or null if not found
    fun getContactByPhone(phoneNumber: String): Contact? {
        val db = readableDatabase
        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,
            null,
            "${ContactContract.ContactEntry.COLUMN_PHONE} = ?",
            arrayOf(phoneNumber),
            null,
            null,
            null
        )

        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            contact = Contact(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME)),
                firstname = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_FIRSTNAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_EMAIL)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ADDRESS)),
                photo = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHOTO))
            )
        }
        cursor.close()
        return contact
    }

    // adds a new message to the database by converting the Message object to ContentValues
    // and inserting it into the messages table, returning the new row ID
    fun insertMessage(message: Message): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MessageContract.MessageEntry.COLUMN_CONTACT_ID, message.contactId)
            put(MessageContract.MessageEntry.COLUMN_MESSAGE, message.message)
            put(MessageContract.MessageEntry.COLUMN_TIMESTAMP, message.timestamp)
            put(MessageContract.MessageEntry.COLUMN_IS_SENT, if (message.isSent) 1 else 0)
        }
        return db.insert(MessageContract.MessageEntry.TABLE_NAME, null, values)
    }

    // retrieves all messages associated with a specific contact ID
    // ordering them chronologically and returning them as a list of Message objects
    fun getMessagesForContact(contactId: Long): List<Message> {
        val messages = mutableListOf<Message>()
        val db = readableDatabase
        val cursor = db.query(
            MessageContract.MessageEntry.TABLE_NAME,
            null,
            "${MessageContract.MessageEntry.COLUMN_CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
            null,
            "${MessageContract.MessageEntry.COLUMN_TIMESTAMP} ASC"
        )

        while (cursor.moveToNext()) {
            val message = Message(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_ID)),
                contactId = cursor.getLong(cursor.getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_CONTACT_ID)),
                message = cursor.getString(cursor.getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_MESSAGE)),
                timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_TIMESTAMP)),
                isSent = cursor.getInt(cursor.getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_IS_SENT)) == 1
            )
            messages.add(message)
        }
        cursor.close()
        return messages
    }
}
