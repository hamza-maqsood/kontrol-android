package com.grayhatdevelopers.kontrol.bindingextensions

import android.view.View
import androidx.databinding.BindingAdapter


object ViewBindings {

    /**
     * binding adapter to change view visibility based on a condition
     */
    @JvmStatic
    @BindingAdapter("bind:goneUnless")
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

}