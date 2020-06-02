package com.grayhatdevelopers.kontrol.utils.sharedprefs

import com.google.gson.Gson
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.utils.AppConstants

class PrefUtils (
    private val prefs: SharedPreferences
) {

    fun getRider(): Rider {
        val json = prefs.getString(AppConstants.SAVED_RIDER, "")
        return Gson().fromJson(json, Rider::class.java)
    }

    fun saveRider(rider: Rider) {
        prefs.saveString(AppConstants.SAVED_RIDER, Gson().toJson(rider))
    }

    fun removeRider() {
        prefs.saveString(AppConstants.SAVED_RIDER, "")
    }

    fun changeRiderLoginStatus(status: Boolean) {
        prefs.saveBoolean(AppConstants.IS_RIDER_LOGGED_IN, status)
    }

    fun isRiderLoggedIn() = prefs.getBoolean(AppConstants.IS_RIDER_LOGGED_IN, false)


    fun getLastFetchTime(): Long =
        prefs.getLong(AppConstants.LAST_FETCH_TIME, 0.toLong())

    fun updateLastFetchTime(time: Long) =
        prefs.saveLong(AppConstants.LAST_FETCH_TIME, time)
}
