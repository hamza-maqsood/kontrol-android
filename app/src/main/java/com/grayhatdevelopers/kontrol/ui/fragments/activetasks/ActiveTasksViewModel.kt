package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.adapters.tasks.TasksAdapter
import com.grayhatdevelopers.kontrol.models.GetTasksRequest
import com.grayhatdevelopers.kontrol.models.task.TaskActions
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class ActiveTasksViewModel : BaseViewModel(), TasksAdapter.OnTaskItemClickedListener {

    @Bindable
    val isDataLoaded: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val activeTasksCount: MutableLiveData<Int> = MutableLiveData()

    private var isDataNotLoaded = true

    init {
        activeTasksCount.postValue(0)
        TasksAdapter.bindOnTaskItemClickListener(listener = this)
        isDataLoaded.postValue(false)
    }

    val activeTaskActions: SingleLiveEvent<ActiveTaskActions> = SingleLiveEvent()
    val taskLoadingResults: SingleLiveEvent<TaskLoadingResults> = SingleLiveEvent()

    fun goBack() {
        // go back to previous fragment
        activeTaskActions.postValue(ActiveTaskActions.GoBack)

    }

    fun activeSearch() {
        // show search options to the user
        activeTaskActions.postValue(ActiveTaskActions.ShowSearchOptions)
    }

    fun showMenu() {
        // show options menu
        activeTaskActions.postValue(ActiveTaskActions.ShowMenuOptions)
    }

    fun selectDate() {
        // show calender to select date from
        activeTaskActions.postValue(ActiveTaskActions.ShowCalenderOptions)
    }

    fun filterTask() {
        // show task filter options
        activeTaskActions.postValue(ActiveTaskActions.ShowFilterOptions)
    }

    fun sortTasks() {
        // show task sort options
        activeTaskActions.postValue(ActiveTaskActions.ShowSortOptions)
    }

    fun checkDataValidity() {
//        val lastFetch: Long = repo.lastFetchAt
//        val now: Long = Calendar.getInstance().time.time
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(now - lastFetch)
//        if (minutes > 15.toLong()) {
        loadTasks()
//        }
    }

    private fun updateLastFetch() {
        repo.updateLastFetch(Calendar.getInstance().time.time)
    }

    private fun loadTasks() {
        if (isDataNotLoaded) {
            viewModelScope.launch {
                val request = GetTasksRequest().apply {
                    this.riders.add(repo.currentUser?.username!!)
                }
                val response = repo.getTasks(getTasksRequest = request)
                isDataLoaded.postValue(true)
                when (response.code()) {
                    AppConstants.OK -> {
                        Log.d(TAG, "HTTP STATUS CODE : ACCEPTED  (202)")
                        // in case of successful task fetch
                        taskLoadingResults.postValue(TaskLoadingResults.LoadingFinished(isSuccessful = true))
                        val tasks = response.body()!!
                        Log.d(TAG, "Fetched Tasks: $tasks")
                        repo.tasks.postValue(tasks)
                        activeTasksCount.postValue(tasks.count())
                        updateLastFetch()
                        isDataNotLoaded = false
                    }

                    AppConstants.NOT_FOUND -> {
                        // in case of no internet connection
                        Log.d(TAG, "HTTP STATUS CODE : NOT_FOUND  (404)")
                        taskLoadingResults.postValue(TaskLoadingResults.LoadingFinished(isSuccessful = false))
                    }

                    AppConstants.UNAUTHORIZED -> {
                        // in case the login session has expired
                        Log.d(TAG, "Login session expired!")
                        repo.logoutUser()
                        taskLoadingResults.postValue(TaskLoadingResults.LoginSessionExpired)
                    }

                    else -> {
                        Log.d(TAG, "Got an unexpected response: ${response.code()}")
                        taskLoadingResults.postValue(TaskLoadingResults.LoadingFinished(isSuccessful = false))
                    }
                }
            }
        }
    }

    override fun onTaskItemClicked(action: TaskActions) {
        when (action) {
            is TaskActions.ExecuteTask -> {
                activeTaskActions.postValue(ActiveTaskActions.ExecuteTask(action.task))
            }
        }
    }

    companion object {
        private const val TAG = "ActiveTasksViewModel"
    }

}