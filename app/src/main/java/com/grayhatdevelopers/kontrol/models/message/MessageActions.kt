package com.grayhatdevelopers.kontrol.models.message

sealed class MessageActions {
    object NetworkError : MessageActions()
    data class OpenImage(val imageURI: String) : MessageActions()
}