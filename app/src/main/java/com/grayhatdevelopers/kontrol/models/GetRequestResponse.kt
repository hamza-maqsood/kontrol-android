package com.grayhatdevelopers.kontrol.models

import com.grayhatdevelopers.kontrol.models.task.Task

data class GetRequestResponse(
    val resultsCount: Int = 0,
    val totalAvailableResults: Int = 0,
    val results: List<Task> = ArrayList()
)