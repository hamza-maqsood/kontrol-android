package com.grayhatdevelopers.kontrol.utils

import android.view.View
import android.widget.LinearLayout


class ViewWeightAnimationWrapper(view: View) {

    private var view: View? = null

    init {
        if (view.layoutParams is LinearLayout.LayoutParams) {
            this.view = view
        } else {
            throw IllegalArgumentException("The view should have LinearLayout as parent")
        }
    }

    var weight: Float
        get() = (view?.layoutParams as LinearLayout.LayoutParams).weight
        set(weight) {
            val params = view?.layoutParams as LinearLayout.LayoutParams
            params.weight = weight
            view?.parent?.requestLayout()
        }
}