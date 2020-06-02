package com.grayhatdevelopers.kontrol.viewutils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd

fun slideViewsHorizontally(
    views: List<View>,
    map: View,
    currentWidth: Int,
    newWidth: Int,
    doAfter: () -> Unit
) {
    val slideAnimator = ValueAnimator.ofInt(
        newWidth, currentWidth
    ).apply {
        duration = 500
        addUpdateListener {
            val value: Int = it.animatedValue as Int
            views.forEach { view ->
                view.layoutParams.width = value
//                view.requestLayout()
            }
        }
    }

    AnimatorSet().apply {
        interpolator = AccelerateDecelerateInterpolator()
        play(slideAnimator)
        start()
        doOnEnd {
            doAfter.invoke()
        }
    }
}