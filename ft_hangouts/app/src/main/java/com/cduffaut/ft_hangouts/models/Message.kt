package com.cduffaut.ft_hangouts.models

data class Message(
    val id: Long = -1,
    val contactId: Long,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSent: Boolean // true if send, false if not
)