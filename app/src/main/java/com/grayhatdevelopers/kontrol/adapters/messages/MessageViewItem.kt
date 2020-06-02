package com.grayhatdevelopers.kontrol.adapters.messages

import com.grayhatdevelopers.kontrol.R

sealed class MessageViewItem(val resource: Int) {
    object Sent: MessageViewItem(R.layout.item_message_sent)
    object Received: MessageViewItem(R.layout.item_message_received)
}