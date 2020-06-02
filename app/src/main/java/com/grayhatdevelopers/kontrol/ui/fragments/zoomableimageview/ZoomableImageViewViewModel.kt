package com.grayhatdevelopers.kontrol.ui.fragments.zoomableimageview

import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent


class ZoomableImageViewViewModel(
    context: Context
) : BaseViewModel(context) {

    val zoomableImageViewActions: SingleLiveEvent<ZoomableImageViewActions> = SingleLiveEvent()

    @Bindable
    val imageURI: MutableLiveData<String> = MutableLiveData()


    fun goBack() {
        zoomableImageViewActions.postValue(ZoomableImageViewActions.GoBack)
    }

    fun initImage(uri: String) {
        imageURI.postValue(uri)
    }
}