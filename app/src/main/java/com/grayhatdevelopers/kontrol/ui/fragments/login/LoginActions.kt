package com.grayhatdevelopers.kontrol.ui.fragments.login

sealed class LoginActions {
    object AlreadyLoggedIn : LoginActions()
    object NotLoggedIn : LoginActions()
    object ProceedLogin : LoginActions()
    object LoginSucceeded : LoginActions()
    object MissingInformation : LoginActions()
    data class LoginFailed(val error: String) : LoginActions()
}