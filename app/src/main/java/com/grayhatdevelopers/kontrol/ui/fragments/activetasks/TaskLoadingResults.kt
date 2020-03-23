package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

sealed class TaskLoadingResults {
    object LoginSessionExpired : TaskLoadingResults()
    object LoadingTasks : TaskLoadingResults()
    data class LoadingFinished(val isSuccessful: Boolean) : TaskLoadingResults()
}