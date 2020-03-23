package com.grayhatdevelopers.kontrol.utils

import android.content.Context
import com.google.gson.Gson
import com.grayhatdevelopers.kontrol.models.Rider

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = SharedPreferences.getInstance(context)

    fun getRider(): Rider {
        val json = sharedPreferences.getString(AppConstants.SAVED_RIDER, "")
        return Gson().fromJson(json, Rider::class.java)
    }

    fun saveRider(rider: Rider) {
        sharedPreferences.saveString(AppConstants.SAVED_RIDER, Gson().toJson(rider))
    }

    fun removeRider() {
        sharedPreferences.saveString(AppConstants.SAVED_RIDER, "")
    }

    fun changeRiderLoginStatus(status: Boolean) {
        sharedPreferences.saveBoolean(AppConstants.IS_RIDER_LOGGED_IN, status)
    }

    fun isRiderLoggedIn() = sharedPreferences.getBoolean(AppConstants.IS_RIDER_LOGGED_IN, false)


    fun getLastFetchTime(): Long =
        sharedPreferences.getLong(AppConstants.LAST_FETCH_TIME, 0.toLong())

    fun updateLastFetchTime(time: Long) =
        sharedPreferences.saveLong(AppConstants.LAST_FETCH_TIME, time)
}