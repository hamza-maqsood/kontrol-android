package com.grayhatdevelopers.kontrol.bindingextensions

import android.text.InputFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.models.message.MessageType
import rm.com.audiowave.AudioWaveView

object MessageBindings {


    /**
     * binding adapter to decide visibility of components in a message
     */
    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: AudioWaveView, messageType: MessageType?) {
        messageType?.let {
            view.visibility = if (messageType == MessageType.AUDIO) View.VISIBLE
            else View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: TextView, messageType: MessageType?) {
        messageType?.let {
            view.visibility = if (messageType == MessageType.TEXT) View.VISIBLE
            else View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: ImageView, messageType: MessageType?) {
        messageType?.let {
//            view.visibility = if (
//                messageType == MessageType.IMAGE ||
//                messageType == MessageType.VIDEO ||
//                messageType == MessageType.AUDIO
//            ) View.VISIBLE
//            else View.GONE

            view.visibility = if (
                messageType == MessageType.IMAGE
                    ) View.VISIBLE
            else View.GONE
        }

    }

    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: ConstraintLayout, messageType: MessageType?) {
        messageType?.let {
            view.visibility = if (messageType != MessageType.TEXT) View.VISIBLE
            else View.GONE
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    @JvmStatic
    @BindingAdapter("bind:decideStartIcon")
    fun decideStartIcon(view: TextView, messageType: MessageType?) {
        messageType?.let {
            when (it) {
                MessageType.FILE -> {
                    view.visibility = View.VISIBLE
                    view.filters = arrayOf(InputFilter.LengthFilter(25))
                    view.text = view.text.toString().substringAfterLast("/")
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.file_icon, 0, 0, 0)
                }
                MessageType.CONTACT -> {
                    view.visibility = View.VISIBLE
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.person_icon, 0, 0, 0)
                }
            }
        }
    }

}