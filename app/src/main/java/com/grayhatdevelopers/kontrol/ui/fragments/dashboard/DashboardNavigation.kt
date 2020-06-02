package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

import com.grayhatdevelopers.kontrol.models.client.ClientsList

sealed class DashboardNavigation {
    object Chat : DashboardNavigation()
    object ActiveTasks : DashboardNavigation()
    object TasksHistory : DashboardNavigation()
    object SignOut : DashboardNavigation()
    data class CreateNewPayment(val clients: ClientsList) : DashboardNavigation()
}