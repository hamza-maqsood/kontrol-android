package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActiveTasksViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ActiveTasksViewModel() as T
    }
}