package com.grayhatdevelopers.kontrol.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Rider(
    @SerializedName(FIELD_USERNAME)
    @Expose
    val username: String = "",
    @SerializedName(FIELD_DISPLAY_NAME)
    @Expose
    val displayName: String = "",
    @SerializedName(FIELD_SESSION_TOKEN)
    @Expose
    val sessionToken: String = "",
    @SerializedName(FIELD_IMAGE_URI)
    @Expose
    val imageURI: String = "",
    @SerializedName(FIELD_CASH_AMOUNT)
    @Expose
    val cashAmount: Int = 0,  /* Cash Amount currently the rider due */
    @SerializedName(FIELD_USER_TYPE)
    @Expose
    val userType: UserType = UserType.RIDER
) {
    companion object {
        const val FIELD_USERNAME = "username"
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_SESSION_TOKEN = "sessionToken"
        const val FIELD_IMAGE_URI = "imageURI"
        const val FIELD_CASH_AMOUNT = "cashAmount"
        const val FIELD_USER_TYPE = "userType"
    }
}

enum class UserType {
    RIDER
}

