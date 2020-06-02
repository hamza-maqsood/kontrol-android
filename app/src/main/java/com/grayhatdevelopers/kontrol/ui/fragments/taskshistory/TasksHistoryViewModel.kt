package com.grayhatdevelopers.kontrol.ui.fragments.taskshistory

import android.content.Context
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent

class TasksHistoryViewModel(
    context: Context
) : BaseViewModel(context) {

    val tasksHistoryActions: SingleLiveEvent<TasksHistoryActions> = SingleLiveEvent()

    fun goBack() {
        tasksHistoryActions.postValue(TasksHistoryActions.GoBack)
    }
}