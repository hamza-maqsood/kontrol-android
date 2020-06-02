package com.grayhatdevelopers.kontrol.bindingextensions

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.grayhatdevelopers.kontrol.utils.DateUtils

object TextViewBindings {

    /**
     * binding adapter to format time from date
     */
    @JvmStatic
    @BindingAdapter("bind:formatTime")
    fun formatTime(view: TextView, date: String?) {
        date?.let {
            view.text = StringBuilder().apply {
                append(DateUtils.formatTimeWithMarker(date.toLong()))
            }.toString()
        }
    }
}