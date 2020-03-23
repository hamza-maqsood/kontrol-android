package com.grayhatdevelopers.kontrol.bindingextensions

import android.view.View
import androidx.databinding.BindingAdapter

object OnTouchEventBindings {

    @JvmStatic
    @BindingAdapter("onTouchListener")
    fun setTouchListener(view: View, listener: View.OnTouchListener) {
        view.setOnTouchListener(listener)
    }
}