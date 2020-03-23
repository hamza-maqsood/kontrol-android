package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

sealed class GetTaskResults {
    data class Failed(val error: String) : GetTaskResults()
    data class Succeeded(val done: Int, val total: Int) : GetTaskResults()
}