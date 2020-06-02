package com.grayhatdevelopers.kontrol.database.payments

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.grayhatdevelopers.kontrol.database.payments.PaymentEntity.Companion.COLUMN_PAYMENT_ID
import com.grayhatdevelopers.kontrol.database.payments.PaymentEntity.Companion.TABLE_NAME
import com.grayhatdevelopers.kontrol.models.payment.PaymentType
import com.grayhatdevelopers.kontrol.models.payment.VerificationStatus
import java.io.Serializable

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [COLUMN_PAYMENT_ID], unique = true)]
)
data class PaymentEntity(
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = COLUMN_PAYMENT_ID)
    var paymentID: String,

    @ColumnInfo(name = COLUMN_TASK_ID)
    var taskID: String,

    @ColumnInfo(name = COLUMN_PAID_AMOUNT)
    var paidAmount: String,

    @ColumnInfo(name = COLUMN_TIME_OF_PAYMENT)
    var timeOfPayment: String,

    @ColumnInfo(name = COLUMN_REMARKS)
    var remarks: String,

    @ColumnInfo(name = COLUMN_IMAGE_URL)
    var imageURL: String,

    @ColumnInfo(name = COLUMN_LOCATION)
    var location: String,

    @ColumnInfo(name = COLUMN_SHOP_NAME)
    var shopName: String,

    @ColumnInfo(name = COLUMN_CLIENT_PHONE_NUMBER)
    var clientPhoneNumber: String,

    @ColumnInfo(name = COLUMN_PAYMENT_TYPE)
    var paymentType: PaymentType,

    @ColumnInfo(name = COLUMN_PAYMENT_MODEL)
    var paymentModel: String,

    @ColumnInfo(name = COLUMN_VERIFICATION_STATUS)
    var verificationStatus: VerificationStatus

) : Serializable {


    companion object {
        const val TABLE_NAME = "Payments_Table"
        const val COLUMN_PAYMENT_ID = "paymentID"
        const val COLUMN_TASK_ID = "taskID"
        const val COLUMN_PAID_AMOUNT = "paidAmount"
        const val COLUMN_TIME_OF_PAYMENT = "timeOfPayment"
        const val COLUMN_REMARKS = "remarks"
        const val COLUMN_IMAGE_URL = "imageURL"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_SHOP_NAME = "shopName"
        const val COLUMN_CLIENT_PHONE_NUMBER = "clientPhoneNumber"
        const val COLUMN_PAYMENT_TYPE = "paymentType"
        const val COLUMN_PAYMENT_MODEL = "paymentModel"
        const val COLUMN_VERIFICATION_STATUS = "verificationStatus"
    }
}