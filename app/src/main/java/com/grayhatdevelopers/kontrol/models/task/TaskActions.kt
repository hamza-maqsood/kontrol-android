package com.grayhatdevelopers.kontrol.models.task

sealed class TaskActions {
    data class ExecuteTask(val task: Task) : TaskActions()
}