package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

sealed class ExecuteTaskActions {
    object ShowVerifyOptions : ExecuteTaskActions()
    object SaveToDraft : ExecuteTaskActions()
    object GoBack : ExecuteTaskActions()
    object VerifyFromClient : ExecuteTaskActions()
    object VerifyFromAdmin : ExecuteTaskActions()
    object VerificationSuccessful : ExecuteTaskActions()
    object Verifying : ExecuteTaskActions()
    data class SendSMS(val message: String, val phoneNumber: String) : ExecuteTaskActions()
}