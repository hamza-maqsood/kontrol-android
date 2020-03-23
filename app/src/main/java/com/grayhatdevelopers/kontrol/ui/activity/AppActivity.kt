package com.grayhatdevelopers.kontrol.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.PhotoChooser
import com.mapbox.mapboxsdk.Mapbox

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        Mapbox.getInstance(this, AppConstants.MAP_BOX_TOKEN)
    }

    companion object {
        interface OnPhotoAddedListener {
            fun onPhotoAdded(uri: Uri)
        }

        private var mOnPhotoAddedListener: OnPhotoAddedListener? = null
        fun bindPhotoAddedListener(listener: OnPhotoAddedListener) {
            mOnPhotoAddedListener = listener
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PhotoChooser.REQUEST_CODE_IMAGE_CAMERA -> {
                    mOnPhotoAddedListener?.onPhotoAdded(PhotoChooser.attachmentUri)
                }

                PhotoChooser.REQUEST_CODE_IMAGE_GALLERY -> {
                    mOnPhotoAddedListener?.onPhotoAdded(data?.data!!)
                }
            }
        }
    }
}