package com.grayhatdevelopers.kontrol.bindingextensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.models.task.TaskStatus
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

object ImageBindings {

    private const val TAG = "ImageBindings"

    /**
     * binding adapter for circular image view
     */
    @JvmStatic
    @BindingAdapter("bind:imageUri")
    fun loadImage(view: CircleImageView, url: String?) {
        Timber.d("Circular ImageView binding adapter called with uri: $url")
        if (!url.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(url)
                .into(view)
        }
    }

    /**
     * binding adapter for circular image view
     */
    @JvmStatic
    @BindingAdapter("bind:loadUserPhoto")
    fun loadUserPhoto(view: CircleImageView, url: String?) {
        Timber.d("Circular ImageView binding adapter called with uri: $url")
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.profile_placeholder)
            .into(view)
    }

    /**
     * binding adapter for image view
     */
    @JvmStatic
    @BindingAdapter("bind:imageUri")
    fun loadImage(view: ImageView, url: String?) {
        Timber.d("ImageView binding adapter called with uri: $url")
        if (!url.isNullOrEmpty()) {
            view.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(view.context)
                .load(url)
                .into(view)
        }
    }

    /**
     * binding adapter for task status icon
     */
    @JvmStatic
    @BindingAdapter("bind:setTaskStatusIcon")
    fun setTaskStatusIcon(view: ImageView?, status: TaskStatus?) {
        status?.let {
            val icon = when (status) {
                TaskStatus.ACTIVE -> {
                    R.drawable.undone_icon
                }
                TaskStatus.COMPLETED -> {
                    R.drawable.double_tick_icon
                }
                TaskStatus.WAITING -> {
                    R.drawable.single_tick_icon
                }
            }
            view?.let {
                Glide.with(view.context)
                    .load(icon)
                    .into(view)
            }
        }
    }
}