package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

sealed class AttachmentPhotoStates {
    object NotSelected : AttachmentPhotoStates()
    data class Selected(val uri: String) : AttachmentPhotoStates()
    object Uploading : AttachmentPhotoStates()
    object Uploaded : AttachmentPhotoStates()
}