package com.grayhatdevelopers.kontrol.models.message

data class Message (
    val messageID: String,
    val participant: String,
    val messageData: String,
    val time: String,
    val messageType: MessageType,
    val isSentByTheUser: Boolean
)