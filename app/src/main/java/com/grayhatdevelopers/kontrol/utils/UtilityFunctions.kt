package com.grayhatdevelopers.kontrol.utils

import android.content.Context
import android.graphics.Point
import android.telephony.SmsManager
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.*


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

fun convertDateToString(date: Date): String {
    val format = SimpleDateFormat(AppConstants.DEFAULT_DATE_TIME_FORMAT, Locale.ENGLISH)
    return format.format(date)
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat(AppConstants.DEFAULT_DATE_TIME_FORMAT, Locale.ENGLISH)
    return sdf.format(Date())
}