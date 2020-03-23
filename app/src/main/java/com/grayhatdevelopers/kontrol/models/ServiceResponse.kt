package com.grayhatdevelopers.kontrol.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ServiceResponse<T>(
    @SerializedName("data") val data: T
)