package com.cduffaut.ft_hangouts.database

class ContactContract {
    object ContactEntry {
        const val TABLE_NAME = "contacts"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_FIRSTNAME = "firstname"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_PHOTO = "photo"
    }
}