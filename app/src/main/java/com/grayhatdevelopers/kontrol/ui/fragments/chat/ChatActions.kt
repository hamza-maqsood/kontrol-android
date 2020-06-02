package com.grayhatdevelopers.kontrol.ui.fragments.chat

sealed class ChatActions {
    object ShowAttachmentsOption : ChatActions()
    object SendMessage : ChatActions()
    object TypingStarted : ChatActions()
    object TypingStopped : ChatActions()
    object RecordingStarted : ChatActions()
    object RecordingFinished : ChatActions()
    object RecordingCanceled : ChatActions()
    object GoBack : ChatActions()
    object PermissionsMissing : ChatActions()
    data class SelectAttachment(val attachmentTypes: AttachmentTypes) : ChatActions()
}