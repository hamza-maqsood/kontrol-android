package com.grayhatdevelopers.kontrol.utils

import android.Manifest
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.grayhatdevelopers.kontrol.R
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * displays a normal toast
 */
fun Context.toast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    val view = toast.view
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        view.background.colorFilter = BlendModeColorFilter(
            ContextCompat.getColor(this, R.color.purple), BlendMode.SRC_ATOP
        )
    } else {
        @Suppress("DEPRECATION")
        view.background.setColorFilter(
            ContextCompat.getColor(this, R.color.purple),
            PorterDuff.Mode.SRC_IN
        )
    }
    val text = view.findViewById<TextView>(android.R.id.message)
    text.setTextColor(ContextCompat.getColor(this, R.color.white))
    toast.show()
}

/**
 * removes the view
 */
fun View.remove() {
    visibility = View.GONE
}

/**
 * makes the view invisible
 */
fun View.hide() {
    visibility = View.INVISIBLE
}

/**
 * makes the view visible
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * validates the email address format
 */
private val EMAIL_ADDRESS_PATTERN: Pattern = Pattern
    .compile(AppConstants.REGEX_EMAIL_ADDRESS)

fun String.isEmailValid(): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(this).matches()
}

/**
 * validates the phone number with Pakistani phone number format
 */
private val PHONE_NUMBER_PATTERN: Pattern = Pattern
    .compile(AppConstants.REGEX_PHONE_NUMBER)

fun String.isPhoneNumberValid(): Boolean {
    return PHONE_NUMBER_PATTERN.matcher(this).matches()
}

/**
 * validates the phone number with Pakistani phone number format
 */
private val ALPHABETICAL_TEXT_PATTERN: Pattern = Pattern
    .compile(AppConstants.REGEX_CONTAIN_NUMBERS)

/**
 * check if the string contain numbers
 */
fun String.doesTextContainNumbers(): Boolean {
    return ALPHABETICAL_TEXT_PATTERN.matcher(this).matches()
}

/**
 * convert string to date object
 */
fun String.convertToDate(): Date {
    val format = SimpleDateFormat(AppConstants.DEFAULT_DATE_TIME_FORMAT, Locale.ENGLISH)
    return format.parse(this)!!
}

/**
 * format your time from long to minutes and seconds
 */
fun formatTimeToMinutesAndSeconds(timeInMillis: Long)
        : String = StringBuilder().apply {
    var seconds = timeInMillis / 1000
    val minutes = seconds / 60

    if (minutes < 10)
        append("0$minutes")
    else append(minutes)
    append(":")
    seconds %= 60
    if (seconds < 10)
        append("0$seconds")
    else append(seconds)
}.toString()

/**
 * send an SMS
 */
fun Context.sendSMS(msg: String, phone: String) = this.runWithPermissions(
    Manifest.permission.SEND_SMS,
    Manifest.permission.READ_PHONE_STATE
) {
    try {   // TODO here
//        val manager = SmsManager.getDefault()
//        manager.sendTextMessage(phone, null, msg, null, null)
        Timber.d("Send sms successfully: ---$phone  ---$msg")
    } catch (ex: Exception) {
        Timber.d(ex)
        ex.printStackTrace()
    }
}

