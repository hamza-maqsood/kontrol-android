package com.grayhatdevelopers.kontrol.repo

import androidx.lifecycle.MutableLiveData
import com.grayhatdevelopers.kontrol.application.MainApplication
import com.grayhatdevelopers.kontrol.models.GetTasksRequest
import com.grayhatdevelopers.kontrol.models.LoginCredentials
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.utils.SharedPreferencesHelper

class Repository private constructor() {


    companion object {
        // @Volatile - Writes to this property are immediately visible to other threads
        @Volatile
        private var instance: Repository? = null

        // The only way to get hold of the Repository object
        fun getInstance() =
        // Already instantiated? - return the instance
            // Otherwise instantiate in a thread-safe manner
            instance
                ?: synchronized(this) {
                    // If it's still not instantiated, finally create an object
                    // also set the "instance" property to be the currently created one
                    instance
                        ?: Repository().also { instance = it }
                }
    }

    private val retrofitAPI = ApiClient.createService(RetrofitDAO::class.java)

    private val sharedPreferencesHelper: SharedPreferencesHelper by lazy {
        SharedPreferencesHelper(MainApplication.context!!)
    }

    val tasks: MutableLiveData<List<Task>> = MutableLiveData()

    val lastFetchAt: Long
        get() {
            return sharedPreferencesHelper.getLastFetchTime()
        }

    var currentUser: Rider? = null

    fun initRider() {
        currentUser = sharedPreferencesHelper.getRider()
        sharedPreferencesHelper.getRider()
    }

    fun updateLastFetch(lastFetch: Long) {
        sharedPreferencesHelper.updateLastFetchTime(lastFetch)
    }

    fun getUserToken(): String = currentUser?.sessionToken ?: ""

    fun isRiderLoggedIn(): Boolean = sharedPreferencesHelper.isRiderLoggedIn()

    private fun changeUserLoginStatus(status: Boolean) {
        sharedPreferencesHelper.changeRiderLoginStatus(status)
    }

    fun logoutUser() {
        currentUser = null
        sharedPreferencesHelper.removeRider()
        changeUserLoginStatus(false)
    }

    suspend fun loginRider(credentials: LoginCredentials) = retrofitAPI.login(credentials)

    suspend fun getTasks(getTasksRequest: GetTasksRequest) =
        retrofitAPI.getRiderTasks(getTasksRequest)

    suspend fun addPaymentToTask(payment: Payment) = retrofitAPI.addPaymentToTask(payment)

    fun updateRider(rider: Rider) {
        changeUserLoginStatus(status = true)
        currentUser = rider
        sharedPreferencesHelper.saveRider(rider)
    }


}