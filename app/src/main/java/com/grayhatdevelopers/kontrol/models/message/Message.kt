package com.grayhatdevelopers.kontrol.models.message

data class Message(
    val id: String = "",
    val senderID: String = "",
    val receiverID: String = "",
    val messageType: MessageType = MessageType.Text(),
    val time: String = "",
    var messageStates: MessageStates
)