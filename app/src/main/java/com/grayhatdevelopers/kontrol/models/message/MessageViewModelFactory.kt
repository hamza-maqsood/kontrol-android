package com.grayhatdevelopers.kontrol.models.message

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity

class MessageViewModelFactory(val message: MessageEntity, val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessageViewModel(context, message) as T
    }
}