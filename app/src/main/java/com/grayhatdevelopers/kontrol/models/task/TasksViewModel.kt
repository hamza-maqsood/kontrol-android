package com.grayhatdevelopers.kontrol.models.task

import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent

class TasksViewModel : BaseViewModel() {

    val taskActions: SingleLiveEvent<TaskActions> = SingleLiveEvent()

    fun executeTask(task: Task) {
        taskActions.postValue(TaskActions.ExecuteTask(task))
    }
}