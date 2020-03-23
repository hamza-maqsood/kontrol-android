package com.grayhatdevelopers.kontrol.models

import com.grayhatdevelopers.kontrol.models.task.TaskModel
import com.grayhatdevelopers.kontrol.models.task.TaskStatus
import com.grayhatdevelopers.kontrol.models.task.TaskType

open class GetTasksRequest(
    val riders: ArrayList<String> = ArrayList(), /* list of riders to include in the list, empty list indicates all riders */
    val dates: ArrayList<String> = ArrayList(), /* list of dates to include in the list, empty list indicates all dates */
    val companies: ArrayList<String> = ArrayList(), /* list of companies to include in the list, empty list indicates all companies */
    val shops: ArrayList<String> = ArrayList(), /* list of shops to include in the list, empty list indicates all shops */
    val taskTypes: ArrayList<TaskType> = ArrayList(), /* list of task types to include in the list, empty list indicates all types */
    val tasksModels: ArrayList<TaskModel> = ArrayList(), /* list of task models to include in the list, empty list indicates all models */
    val taskStatuses: ArrayList<TaskStatus> = ArrayList() /* list of task statues to include in the list, empty list indicates all statuses */
)