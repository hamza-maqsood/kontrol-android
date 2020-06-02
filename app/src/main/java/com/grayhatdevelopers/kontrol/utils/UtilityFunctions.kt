package com.grayhatdevelopers.kontrol.utils

import android.content.Context
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.WindowManager
import timber.log.Timber
import java.util.*
import kotlin.random.Random


fun getScreenHeightPx(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
}

fun getScreenWidthPx(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

fun generateItemId(prefix: String) = StringBuilder().apply {
    append(prefix)
    append("_")
    append(UniqueIdGenerator.getUniqueId().toString())
}.toString()

fun generateVerificationCode() : String = String.format("%04d", Random(Date().time).nextInt(10000))

fun isInternetAvailable(context: Context): Boolean {

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION")
        val networkInfo=connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION")
        return  networkInfo!=null && networkInfo.isConnected
    } else {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Timber.i("NetworkCapabilities.TRANSPORT_CELLULAR")
                    true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Timber.i("NetworkCapabilities.TRANSPORT_WIFI")
                    true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Timber.i("NetworkCapabilities.TRANSPORT_ETHERNET")
                    true
                }
                else -> false
            }
        }
        return false
    }
}

fun generateChequeID() = StringBuilder().apply {
    append("C_")
    append(generateVerificationCode())
}.toString()