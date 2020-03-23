package com.grayhatdevelopers.kontrol.models

data class LoginCredentials(
    val username: String,   /* username */
    val password: String    /* password */
) {
    override fun toString() = "$username -- $password"
}