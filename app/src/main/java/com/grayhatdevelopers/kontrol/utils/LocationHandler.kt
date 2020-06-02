package com.grayhatdevelopers.kontrol.utils

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.grayhatdevelopers.kontrol.models.Location
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object LocationHandler {

    fun getUserCurrentLocation()  : Location = Location()

    suspend fun checkIfLocationIsEnabled(context: Activity) : Boolean {
        val builder = LocationSettingsRequest.Builder()
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        try {
            client.checkLocationSettings(builder.build()).await()
            Timber.d("Location settings were turned on!")
            return true
        } catch (exception: Exception) {
            Timber.d("Location Settings were not turned on")
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    Timber.d("Requesting Location Access")
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        context,
                        LOCATION_TURN_ON_REQUEST
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d(sendEx)
                    return false
                }
            }
            return false
        }
    }

    const val LOCATION_TURN_ON_REQUEST = 109
}