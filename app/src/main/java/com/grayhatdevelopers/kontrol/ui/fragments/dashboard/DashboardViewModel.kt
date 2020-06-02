package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.models.client.ClientsList
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber

class DashboardViewModel(
    context: Context
) : BaseViewModel(context) {

    val dashboardNavigation: SingleLiveEvent<DashboardNavigation> = SingleLiveEvent()
    val dashboardActions: SingleLiveEvent<DashboardActions> = SingleLiveEvent()
    val getTaskResults: SingleLiveEvent<GetTaskResults> = SingleLiveEvent()

    @Bindable
    val tasksRatio: MutableLiveData<String> = MutableLiveData()

    init {
        tasksRatio.postValue("0 / 0")
    }

    fun openCreateNewPaymentFragment() {
        dashboardActions.postValue(DashboardActions.DownloadClientsData)
        if (isInternetAvailable(kodein.direct.instance())) {
            getClientsData()
        } else {
            dashboardActions.postValue(DashboardActions.NetworkError)
        }
    }

    fun openActiveTasksFragment() {
        dashboardNavigation.postValue(DashboardNavigation.ActiveTasks)
    }

    fun openTasksHistoryFragment() {
        dashboardNavigation.postValue(DashboardNavigation.TasksHistory)
    }

    fun openChatFragment() {
        dashboardNavigation.postValue(DashboardNavigation.Chat)
    }

    fun getRider(): Rider = repo.currentUser!!

    fun logoutUser() {
        // remove all previous user records
        repo.logoutUser()
        // post value
        dashboardNavigation.postValue(DashboardNavigation.SignOut)
    }

    private fun getClientsData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = repo.getClientsData()
                when (response.code()) {
                    AppConstants.OK -> {
                        Timber.d("HTTP STATUS CODE : ACCEPTED  (202)")
                        val clients = response.body()!!
                        withContext(Dispatchers.Main) {
                            Timber.d("Request results: $clients")
                            val list = ClientsList()
                            list.addAll(clients)
                            dashboardNavigation.postValue(DashboardNavigation.CreateNewPayment(clients = list))
                        }
                    }

                    AppConstants.UNAUTHORIZED -> {
                        Timber.d("Login session expired!")
                        repo.logoutUser()
                        dashboardActions.postValue(DashboardActions.LoginSessionExpired)
                    }
                    else -> {
                        Timber.d("Got an unexpected response: ${response.code()}")
                        dashboardActions.postValue(DashboardActions.NetworkError)
                    }
                }
            }
        }
    }

}
