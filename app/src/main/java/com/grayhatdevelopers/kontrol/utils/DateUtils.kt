package com.grayhatdevelopers.kontrol.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class with static util methods.
 *  got this code in java from github(sendbird's repo), added a few more functions myself
 */

object DateUtils {

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    fun formatTime(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    fun formatTimeWithMarker(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    fun getHourOfDay(timeInMillis: Long): Int {
        val dateFormat = SimpleDateFormat("H", Locale.getDefault())
        return Integer.valueOf(dateFormat.format(timeInMillis))
    }

    fun getCurrentMillis() : Long = Date().time

    fun getMinute(timeInMillis: Long): Int {
        val dateFormat = SimpleDateFormat("m", Locale.getDefault())
        return Integer.valueOf(dateFormat.format(timeInMillis))
    }

    fun getFormattedDate(day: Int, month: Int, year: Int, pattern: String) : String {
        val date =  GregorianCalendar(year, month - 1, day).time
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun convertDateToString(date: Date): String {
        val format = SimpleDateFormat(AppConstants.DEFAULT_DATE_TIME_FORMAT, Locale.ENGLISH)
        return format.format(date)
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat(AppConstants.DEFAULT_DATE_TIME_FORMAT, Locale.ENGLISH)
        return sdf.format(Date())
    }

    fun getTimeForFilename() : String {
        val sdf = SimpleDateFormat("dd_MM_YY__hh_mm_ss", Locale.ENGLISH)
        return sdf.format(Date())
    }

    fun getToday() : String {
        val sdf = SimpleDateFormat(AppConstants.DEFAULT_DATE_FORMAT, Locale.ENGLISH)
        return sdf.format(Date())
    }

    fun getYesterday() : String {
        val calender = Calendar.getInstance()
        calender.add(Calendar.DATE, -1)
        val yesterday = calender.time
        val sdf = SimpleDateFormat(AppConstants.DEFAULT_DATE_FORMAT, Locale.ENGLISH)
        return sdf.format(yesterday)
    }


    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     * @param timeInMillis  The time to convert, in milliseconds.
     * @return  The time or date.
     */
    fun formatDateTime(timeInMillis: Long): String {
        return if (isToday(timeInMillis)) {
            formatTime(timeInMillis)
        } else {
            formatDate(timeInMillis)
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    fun formatDate(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    fun isToday(timeInMillis: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(timeInMillis)
        return date == dateFormat.format(System.currentTimeMillis())
    }

    /**
     * Checks if two dates are of the same day.
     * @param millisFirst   The time in milliseconds of the first date.
     * @param millisSecond  The time in milliseconds of the second date.
     * @return  Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    fun hasSameDate(millisFirst: Long, millisSecond: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(millisFirst) == dateFormat.format(millisSecond)
    }
}// This class should not be initialized