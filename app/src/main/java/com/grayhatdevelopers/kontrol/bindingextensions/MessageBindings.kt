package com.grayhatdevelopers.kontrol.bindingextensions

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.grayhatdevelopers.kontrol.models.message.MessageType
import rm.com.audiowave.AudioWaveView

object MessageBindings {


    /**
     * binding adapter to decide visibility of components in a message
     */
    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: AudioWaveView, messageType: MessageType) {
        view.visibility = if (messageType is MessageType.Audio) View.VISIBLE
        else View.GONE
    }

    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: TextView, messageType: MessageType) {
        view.visibility = if (messageType is MessageType.Text) View.VISIBLE
        else View.GONE
    }

    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: ImageView, messageType: MessageType) {
        view.visibility = if (
            messageType is MessageType.Image ||
            messageType is MessageType.Video ||
            messageType is MessageType.Audio
        ) View.VISIBLE
        else View.GONE
    }

    @JvmStatic
    @BindingAdapter("bind:decideVisibility")
    fun decideVisibility(view: ConstraintLayout, messageType: MessageType) {
        view.visibility = if (messageType !is MessageType.Text) View.VISIBLE
        else View.GONE
    }

}