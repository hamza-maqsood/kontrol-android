package com.grayhatdevelopers.kontrol.database

import androidx.room.TypeConverter
import com.grayhatdevelopers.kontrol.models.message.MessageState
import com.grayhatdevelopers.kontrol.models.message.MessageType
import com.grayhatdevelopers.kontrol.models.payment.PaymentType
import com.grayhatdevelopers.kontrol.models.payment.VerificationStatus
import com.grayhatdevelopers.kontrol.models.task.TaskStatus

object TypeConverters {

    @JvmStatic
    @TypeConverter
    fun taskStatusToString(taskStatus: TaskStatus) : String = taskStatus.toString()

    @JvmStatic
    @TypeConverter
    fun taskStatusFromString(taskStatus: String) : TaskStatus = taskStatus.let { TaskStatus.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun verificationStatusToString(verificationStatus: VerificationStatus) : String = verificationStatus.toString()

    @JvmStatic
    @TypeConverter
    fun verificationStatusFromString(verificationStatus: String) : VerificationStatus = verificationStatus.let { VerificationStatus.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun messageStatesToString(messageState: MessageState) : String = messageState.toString()

    @JvmStatic
    @TypeConverter
    fun messageStatesFromString(messageState: String) : MessageState = messageState.let { MessageState.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun messageTypeToString(messageType: MessageType) : String = messageType.toString()

    @JvmStatic
    @TypeConverter
    fun messageTypeFromString(messageType: String) : MessageType = messageType.let { MessageType.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun paymentTypeToString(paymentType: PaymentType) : String = paymentType.toString()

    @JvmStatic
    @TypeConverter
    fun paymentTypeFromString(paymentType: String) : PaymentType = paymentType.let { PaymentType.valueOf(it) }

}