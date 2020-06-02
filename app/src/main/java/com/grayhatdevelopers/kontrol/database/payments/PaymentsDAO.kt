package com.grayhatdevelopers.kontrol.database.payments

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface PaymentsDAO {

    @Insert
    fun insertPayment(paymentEntity: PaymentEntity)
}