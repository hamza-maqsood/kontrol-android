package com.grayhatdevelopers.kontrol.utils.sharedprefs

import android.annotation.SuppressLint
import android.content.Context

class SharedPreferences {
    private val prefs: android.content.SharedPreferences
    private val editor: android.content.SharedPreferences.Editor
    private val filename = "preferences"

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
        return prefs.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String?): String? {
        return if (defaultValue != null) prefs.getString(key, defaultValue)!!
            .trim { it <= ' ' } else null
    }

    /*
        Saving methods
    */

    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun saveString(key: String, value: String?) {
        editor.putString(key, value?.trim { it <= ' ' })
        editor.apply()
    }

    fun saveLong(key: String, value: Long) {
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
            mInstance =
                SharedPreferences(
                    context
                )
            return mInstance
        }

    }
}