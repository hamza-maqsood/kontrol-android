package com.grayhatdevelopers.kontrol.models.drafts

import com.grayhatdevelopers.kontrol.models.task.Task

data class Draft (
    val task: Task,
    val payment: Task,
    val isDate: Boolean,
    val date: String
)