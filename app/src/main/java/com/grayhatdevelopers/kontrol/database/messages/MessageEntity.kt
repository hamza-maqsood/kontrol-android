package com.grayhatdevelopers.kontrol.database.messages

import androidx.annotation.NonNull
import androidx.room.*
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity.Companion.COLUMN_MESSAGE_ID
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity.Companion.TABLE_NAME
import com.grayhatdevelopers.kontrol.models.message.Message
import com.grayhatdevelopers.kontrol.models.message.MessageMediaState
import com.grayhatdevelopers.kontrol.models.message.MessageState
import com.grayhatdevelopers.kontrol.models.message.MessageType
import java.io.Serializable

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [COLUMN_MESSAGE_ID], unique = true)]
)
data class MessageEntity (
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = COLUMN_MESSAGE_ID)
    var messageID: String,

    @ColumnInfo(name = COLUMN_PARTICIPANT)
    var participant: String,

    @ColumnInfo(name = COLUMN_TIME)
    var time: String,

    @ColumnInfo(name = COLUMN_IS_MESSAGE_SENT)
    var isMessageSent: Boolean,

    @ColumnInfo(name = COLUMN_MESSAGE_TYPE)
    var messageType: MessageType,

    @ColumnInfo(name = COLUMN_MESSAGE_DATA)
    var messageData: String,

    @ColumnInfo(name = COLUMN_MESSAGE_STATE)
    var messageState: MessageState,

    @ColumnInfo(name = COLUMN_MESSAGE_MEDIA_STATE)
    @Ignore
    var messageMediaState: MessageMediaState = MessageMediaState.NIL

) :
    Serializable {

    constructor() : this(
            "",
            "",
            "",
            false,
            MessageType.TEXT,
            "",
            MessageState.QUEUED,
            MessageMediaState.NIL
    )

    companion object {
        const val TABLE_NAME = "messages_table"
        const val COLUMN_MESSAGE_ID = "messageID"
        const val COLUMN_PARTICIPANT = "participant"
        const val COLUMN_TIME = "time"
        const val COLUMN_IS_MESSAGE_SENT = "isMessageSent"
        const val COLUMN_MESSAGE_TYPE = "messageType"
        const val COLUMN_MESSAGE_STATE = "messageState"
        const val COLUMN_MESSAGE_DATA = "messageData"
        const val COLUMN_MESSAGE_MEDIA_STATE = "messageMediaState"
    }

    fun toSimpleMessage() : Message = Message(
        messageID = messageID,
        participant = participant,
        messageData = messageData,
        time = time,
        messageType = messageType,
        isSentByTheUser = true
    )
}
