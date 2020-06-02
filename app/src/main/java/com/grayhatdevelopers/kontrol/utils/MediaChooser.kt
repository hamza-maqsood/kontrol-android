package com.grayhatdevelopers.kontrol.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.grayhatdevelopers.kontrol.R
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.michaelbel.bottomsheet.BottomSheet
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SameParameterValue")
class MediaChooser(private val context: Activity) {

    fun openAttachmentBottomSheet() {

        val builder = BottomSheet.Builder(context)
        builder.setTitle("CHOOSE FROM")
            .setContentType(BottomSheet.GRID)
            .setFullWidth(true)
            .setItems(items_camera_gallery, icons_camera_gallery)
            { _, which ->
                if (which == 0) {
                    openCameraImage()
                } else if (which == 1) {
                    openGalleryImage()
                }
            }.show()
    }

    fun selectFile() = context.runWithPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
        }
        context.startActivityForResult(intent, REQUEST_CODE_FILE)
    }

    fun selectContact() = context.runWithPermissions(
        Manifest.permission.READ_CONTACTS
    ) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        context.startActivityForResult(intent, REQUEST_CODE_CONTACT)
    }

    private fun openGalleryImage() = context.runWithPermissions(
        Manifest.permission.CAMERA
        , Manifest.permission.READ_EXTERNAL_STORAGE
    ) {

        val intent =
            Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        context.startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CODE_IMAGE_GALLERY
        )
    }

    private fun openCameraImage() = context.runWithPermissions(
        Manifest.permission.CAMERA
        , Manifest.permission.READ_EXTERNAL_STORAGE
    ) {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try {
            attachmentUri =
                FileProvider.getUriForFile(
                    context.applicationContext, PROVIDER_NAME,
                    createFileImageAudioVideo("IMAGE", ".jpg")
                )
            intent.putExtra("output", attachmentUri)
            context.startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA)
        } catch (e: IOException) {
            Toast.makeText(
                context.applicationContext,
                "Error Capturing Photo From Camera",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createFileImageAudioVideo(str: String, str2: String): File {
        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val stringBuilder = StringBuilder()
        stringBuilder.append("PKCP_")
        stringBuilder.append(str)
        stringBuilder.append("_")
        stringBuilder.append(format)
        stringBuilder.append("_")
        return File.createTempFile(
            stringBuilder.toString(),
            str2,
            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        )
    }

    companion object {

        private val items_camera_gallery = arrayOf("Camera", "Gallery")
        private val icons_camera_gallery =
            intArrayOf(R.drawable.camera_icon, R.drawable.gallery_icon)
        private const val PROVIDER_NAME = "kontrol.application"

        const val REQUEST_CODE_IMAGE_GALLERY = 1
        const val REQUEST_CODE_IMAGE_CAMERA = 11
        const val REQUEST_CODE_CONTACT = 33
        const val REQUEST_CODE_FILE = 44

        var attachmentUri: Uri = Uri.EMPTY
    }

    enum class MediaType {
        IMAGE, VIDEO, CONTACT, FILE
    }
}
