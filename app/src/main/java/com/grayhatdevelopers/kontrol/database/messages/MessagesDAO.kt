package com.grayhatdevelopers.kontrol.database.messages

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.grayhatdevelopers.kontrol.models.message.MessageState

@Dao
interface MessagesDAO {

    @Insert
    fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM ${MessageEntity.TABLE_NAME} WHERE ${MessageEntity.COLUMN_PARTICIPANT} = :participant")
    fun getMessagesByParticipant(participant: String) : LiveData<List<MessageEntity>>

    @Query("UPDATE ${MessageEntity.TABLE_NAME} SET ${MessageEntity.COLUMN_MESSAGE_STATE} = :newMessageState WHERE ${MessageEntity.COLUMN_MESSAGE_ID} = :messageID")
    fun updateMessageState(messageID: String, newMessageState: MessageState)
}