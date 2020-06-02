package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

import com.grayhatdevelopers.kontrol.models.task.Task

sealed class ActiveTaskActions {
    object ShowSortOptions : ActiveTaskActions()
    object ShowFilterOptions : ActiveTaskActions()
    object ShowCalenderOptions : ActiveTaskActions()
    object ShowSearchOptions : ActiveTaskActions()
    object ShowMenuOptions : ActiveTaskActions()
    object GoBack : ActiveTaskActions()
    object InternetConnectionError: ActiveTaskActions()
    data class ExecuteTask(val task: Task) : ActiveTaskActions()
}