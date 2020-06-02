package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

sealed class DashboardActions {
    object DownloadTask : DashboardActions()
    object DownloadClientsData : DashboardActions()
    object DismissDialog : DashboardActions()
    object LoginSessionExpired : DashboardActions()
    object NetworkError : DashboardActions()
}