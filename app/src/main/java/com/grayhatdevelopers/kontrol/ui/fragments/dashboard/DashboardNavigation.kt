package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

sealed class DashboardNavigation {
    object Chat : DashboardNavigation()
    object ActiveTasks : DashboardNavigation()
    object CreateNewPayment : DashboardNavigation()
    object TasksHistory : DashboardNavigation()
    object SignOut : DashboardNavigation()
}