package com.grayhatdevelopers.kontrol.models.client

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(
    val name: String = "",
    val balance: Double = 0.toDouble(),
    val paidAmount: Double = 0.toDouble()
) : Parcelable