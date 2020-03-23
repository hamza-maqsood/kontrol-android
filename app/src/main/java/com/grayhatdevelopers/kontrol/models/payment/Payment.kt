package com.grayhatdevelopers.kontrol.models.payment

import androidx.room.Entity
import com.grayhatdevelopers.kontrol.models.LatLng
import com.grayhatdevelopers.kontrol.models.payment.Payment.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
data class Payment(
    val _id: String, /* payment unique id */
    val taskID: String, /* same as the associated task's 'id */
    val paidAmount: String, /* amount that was paid to the rider */
    val timeOfPayment: String?, /* time of payment */
    val remarks: String, /* remarks by the rider */
    val imageURL: String?, /* imageURI, if any added */
    val location: LatLng, /* location point of the rider when the payment was added */
    val shopName: String,
    val clientPhoneNumber: String = "",
    val paymentType: String,
    val paymentModel: String,
    var verificationStatus: VerificationStatus = VerificationStatus.NOT_INIT /* payment verification status */
) {
    companion object {
        const val TABLE_NAME = "Payments_Table"
    }
}

enum class VerificationStatus {
    NOT_INIT, ADMIN_PENDING, ADMIN_VERIFIED, CLIENT_VERIFIED
}