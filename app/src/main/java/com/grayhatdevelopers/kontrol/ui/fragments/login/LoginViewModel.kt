package com.grayhatdevelopers.kontrol.ui.fragments.login

import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.models.LoginCredentials
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.isInternetAvailable
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber

class LoginViewModel(
    context: Context
) : BaseViewModel(context) {

    val loginActions: SingleLiveEvent<LoginActions> = SingleLiveEvent()

    @Bindable
    val enteredUsername = MutableLiveData<String>()

    @Bindable
    val enteredPassword = MutableLiveData<String>()

    fun checkLoginStatus() {
        val loginStatus = repo.isRiderLoggedIn()
        if (loginStatus) { // user already logged in
            repo.initRider()
            loginActions.postValue(LoginActions.AlreadyLoggedIn)
        } else { // no user logged in
            loginActions.postValue(LoginActions.NotLoggedIn)
        }
    }

    fun proceedLogin() {
        val username = enteredUsername.value
        val password = enteredPassword.value
        if (username.isNullOrBlank() || password.isNullOrBlank())
        // either password or username was missing
            loginActions.postValue(LoginActions.MissingInformation)
        else {
            if (isInternetAvailable(kodein.direct.instance())) {
                // proceed login
                loginActions.postValue(LoginActions.ProceedLogin)
                login(username, password)
            } else {
                // internet connection error
                loginActions.postValue(LoginActions.LoginFailed("Make sure you've a stable internet connection!"))
            }
        }
    }

    private fun login(username: String, password: String) {
        viewModelScope.launch {
            val response = repo.loginRider(LoginCredentials(username, password))
            when (response.code()) {
                AppConstants.ACCEPTED -> {
                    Timber.d("HTTP STATUS CODE : ACCEPTED  (202)")
                    // in case of successful login
                    val rider = response.body()!!
                    repo.updateRider(rider)
                    loginActions.postValue(LoginActions.LoginSucceeded)
                }

                AppConstants.NOT_FOUND -> {
                    // in case of no internet connection
                    Timber.d("HTTP STATUS CODE : NOT_FOUND  (404)")
                    loginActions.postValue(LoginActions.LoginFailed("Make sure you have a stable internet connection!"))
                }

                AppConstants.UNAUTHORIZED -> {
                    // in case of invalid login credentials
                    Timber.d("HTTP STATUS CODE : UNAUTHORIZED  (401)")
                    loginActions.postValue(LoginActions.LoginFailed("Invalid username/password"))
                }
            }
        }
    }
}