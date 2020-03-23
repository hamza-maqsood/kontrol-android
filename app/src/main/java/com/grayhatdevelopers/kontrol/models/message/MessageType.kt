package com.grayhatdevelopers.kontrol.models.message

sealed class MessageType {
    data class Text(val message: String = "") : MessageType()
    data class Image(val imageUri: String = "") : MessageType()
    data class Video(val videoUri: String = "") : MessageType()
    data class File(val fileUri: String = "") : MessageType()
    data class Contact(val contactUri: String = "") : MessageType()
    data class Audio(val audioUri: String = "") : MessageType()
}