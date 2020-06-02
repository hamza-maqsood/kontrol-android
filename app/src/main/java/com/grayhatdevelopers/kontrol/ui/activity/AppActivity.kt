package com.grayhatdevelopers.kontrol.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.LocationHandler
import com.grayhatdevelopers.kontrol.utils.MediaChooser
import com.mapbox.mapboxsdk.Mapbox
import com.theartofdev.edmodo.cropper.CropImage
import timber.log.Timber


class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        Mapbox.getInstance(this@AppActivity, AppConstants.MAP_BOX_TOKEN)

        // move the view up, when the keyboard is active
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun performCropImage(imageUri: Uri) {
        CropImage.activity(imageUri)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    mOnMediaAddedListener?.onMediaAdded(result.uri, MediaChooser.MediaType.IMAGE)
                }

                MediaChooser.REQUEST_CODE_IMAGE_CAMERA -> {
                    performCropImage(MediaChooser.attachmentUri)
                }

                MediaChooser.REQUEST_CODE_IMAGE_GALLERY -> {
                    performCropImage(data?.data!!)
                }

                MediaChooser.REQUEST_CODE_CONTACT -> {

                    var number = ""
                    var name = ""
                    val contactUri = data?.data ?: return
                    val projection = arrayOf(
                            Phone.DISPLAY_NAME,
                            Phone.NUMBER
                    )
                    val cursor = contentResolver.query(
                            contactUri,
                            projection,
                            null,
                            null,
                            null
                    )

                    if (cursor != null && cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME)
                        val numberIndex = cursor.getColumnIndex(Phone.NUMBER)
                        name = cursor.getString(nameIndex)
                        number = cursor.getString(numberIndex)
                    }
                    cursor?.close()
                        val contact = StringBuilder().apply {
                            append("Name: $name\n")
                            append("Phone: $number")
                        }.toString()
                    mOnMediaAddedListener?.onMediaAdded(Uri.parse(contact), MediaChooser.MediaType.CONTACT)
                }

                MediaChooser.REQUEST_CODE_FILE -> {
                    mOnMediaAddedListener?.onMediaAdded(data?.data!!, MediaChooser.MediaType.FILE)
                }

                LocationHandler.LOCATION_TURN_ON_REQUEST -> {
                    when (resultCode) {
                        Activity.RESULT_OK ->  {
                            Timber.d("Location's turned ON")
                            mOnLocationEnabledListener?.onLocationEnabledListener(enabled = true)
                        }
                        Activity.RESULT_CANCELED -> {
                            Timber.d("Location was not turned on by the user!")
                            mOnLocationEnabledListener?.onLocationEnabledListener(enabled = false)
                        }
                    }
                }
            }
        }
    }

    interface OnMediaAddedListener {
        fun onMediaAdded(uri: Uri, mediaType: MediaChooser.MediaType)
    }

    interface OnLocationEnabledListener {
        fun onLocationEnabledListener(enabled: Boolean)
    }

    companion object {

        private var mOnMediaAddedListener: OnMediaAddedListener? = null
        fun bindMediaAddedListener(listener: OnMediaAddedListener) {
            mOnMediaAddedListener = listener
        }

        private var mOnLocationEnabledListener: OnLocationEnabledListener? = null
        fun bindLocationEnabledListener(listener: OnLocationEnabledListener?) {
            mOnLocationEnabledListener = listener
        }
    }
}