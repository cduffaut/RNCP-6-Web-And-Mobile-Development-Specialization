package com.cduffaut.ft_hangouts.database

class MessageContract {
    object MessageEntry {
        const val TABLE_NAME = "messages"
        const val COLUMN_ID = "_id"
        const val COLUMN_CONTACT_ID = "contact_id"
        const val COLUMN_MESSAGE = "message"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IS_SENT = "is_sent" // true if send, false if received
    }
}