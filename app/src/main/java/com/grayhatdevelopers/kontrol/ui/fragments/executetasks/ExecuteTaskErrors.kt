package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

sealed class ExecuteTaskErrors {
    object ReceivedAmountNotEntered : ExecuteTaskErrors()
    object VerificationMethodNotSelected : ExecuteTaskErrors()
    object NetworkError : ExecuteTaskErrors()
    object MediaPermissionsNotGranted : ExecuteTaskErrors()
    object SMSVerificationFailed : ExecuteTaskErrors()
}