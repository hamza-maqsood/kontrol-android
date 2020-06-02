package com.grayhatdevelopers.kontrol.models.task

import android.content.Context
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent

class TasksViewModel(
    context: Context
) : BaseViewModel(context) {

    val taskActions: SingleLiveEvent<TaskActions> = SingleLiveEvent()

    fun executeTask(task: Task) {
        taskActions.postValue(TaskActions.ExecuteTask(task))
    }
}