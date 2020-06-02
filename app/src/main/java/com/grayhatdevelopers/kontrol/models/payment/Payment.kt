package com.grayhatdevelopers.kontrol.models.payment

import com.google.gson.Gson
import com.grayhatdevelopers.kontrol.database.payments.PaymentEntity
import com.grayhatdevelopers.kontrol.models.Location
import com.grayhatdevelopers.kontrol.utils.DateUtils

data class Payment(
    val _id: String, /* payment unique id */
    val taskID: String, /* same as the associated task's 'id */
    val paidAmount: String, /* amount that was paid to the rider */
    val timeOfPayment: String?, /* time of payment */
    val remarks: String, /* remarks by the rider */
    val imageURL: String?, /* imageURI, if any added */
    val location: Location, /* location point of the rider when the payment was added */
    val shopName: String,
    val clientPhoneNumber: String = "",
    val paymentType: PaymentType,
    val paymentModel: String,
    val chequeID: String,
    var verificationStatus: VerificationStatus = VerificationStatus.NOT_INIT /* payment verification status */
) {

    fun toPaymentEntity() : PaymentEntity = PaymentEntity(
        paymentID = _id,
        taskID = taskID,
        paidAmount = paidAmount,
        timeOfPayment = timeOfPayment ?: DateUtils.getCurrentTime(),
        remarks = remarks,
        imageURL = imageURL ?: "Not Available",
        location = Gson().toJson(location),
        shopName = shopName,
        clientPhoneNumber = clientPhoneNumber,
        paymentType = paymentType,
        paymentModel = paymentModel,
        verificationStatus = verificationStatus
    )

}