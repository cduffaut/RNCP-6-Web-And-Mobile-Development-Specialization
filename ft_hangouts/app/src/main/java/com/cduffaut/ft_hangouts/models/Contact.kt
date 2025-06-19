package com.cduffaut.ft_hangouts.models

data class Contact(
    val id: Long = -1,
    val name: String,
    val firstname: String = "",
    val phone: String,
    val email: String = "",
    val address: String = "",
    val photo: String = "" // path for profile picture
)