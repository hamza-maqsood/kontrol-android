package com.grayhatdevelopers.kontrol.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

class SharedPreferences {
    private val prefs: android.content.SharedPreferences
    private val editor: android.content.SharedPreferences.Editor
    private val filename = "preferences"
    var isLoggingEnabled = false
        private set

    @SuppressLint("CommitPrefEdits")
    private constructor(context: Context) {
        prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    @SuppressLint("CommitPrefEdits")
    private constructor(context: Context, filename: String) {
        prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    operator fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    /*
        Retrieving methods
     */

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        if (isLoggingEnabled)
            Log.d(TAG, "Value: " + key + " is " + prefs.getBoolean(key, defaultValue))
        return prefs.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String?): String? {
        if (isLoggingEnabled)
            Log.d(
                TAG,
                "Value: $key is " + if (defaultValue != null) prefs.getString(
                    key,
                    defaultValue
                )!!.trim { it <= ' ' } else null)
        return if (defaultValue != null) prefs.getString(key, defaultValue)!!
            .trim { it <= ' ' } else null
    }

    /*
        Saving methods
    */

    fun saveBoolean(key: String, value: Boolean) {
        if (isLoggingEnabled)
            Log.d(TAG, "Saving $key with value $value")
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun saveString(key: String, value: String?) {
        if (isLoggingEnabled)
            Log.d(TAG, "Saving $key with value $value")
        editor.putString(key, value?.trim { it <= ' ' })
        editor.apply()
    }

    fun saveLong(key: String, value: Long) {
        if (isLoggingEnabled)
            Log.d(TAG, "Saving $key with value $value")
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    companion object {

        const val TAG = "SharedPreferences"

        private lateinit var mInstance: SharedPreferences

        fun getInstance(context: Context): SharedPreferences {
            mInstance = SharedPreferences(context)
            return mInstance
        }

    }
}