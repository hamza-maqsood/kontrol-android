package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent

class DashboardViewModel : BaseViewModel() {

    val dashboardNavigation: SingleLiveEvent<DashboardNavigation> = SingleLiveEvent()
    val dashboardActions: SingleLiveEvent<DashboardActions> = SingleLiveEvent()
    val getTaskResults: SingleLiveEvent<GetTaskResults> = SingleLiveEvent()

    @Bindable
    val tasksRatio: MutableLiveData<String> = MutableLiveData()

    init {
        tasksRatio.postValue("0 / 0")
    }

    fun openCreateNewPaymentFragment() {
        dashboardNavigation.postValue(DashboardNavigation.CreateNewPayment)
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

}
