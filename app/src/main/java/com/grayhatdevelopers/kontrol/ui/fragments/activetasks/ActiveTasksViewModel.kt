package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

import android.content.Context
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.adapters.tasks.TasksAdapter
import com.grayhatdevelopers.kontrol.models.GetTasksRequest
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.models.task.TaskActions
import com.grayhatdevelopers.kontrol.models.task.TaskStatus
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecutePaymentsBaseViewModel
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.DateUtils
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.isInternetAvailable
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber
import java.util.*


class ActiveTasksViewModel(
    context: Context
) : BaseViewModel(context),
    TasksAdapter.OnTaskItemClickedListener,
    ExecutePaymentsBaseViewModel.OnTasksStatusUpdatedListener {

    @Bindable
    val isDataLoaded: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val enteredSearchQuery: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val dateText: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val activeTasksCount: MutableLiveData<Int> = MutableLiveData()

    @Bindable
    val isSearchActive: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val showFilterOptions: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val showSortOptions: MutableLiveData<Boolean> = MutableLiveData()

    val activeTasks: MutableLiveData<List<Task>> = MutableLiveData()

    private var isDataNotLoaded = true
    private var sortOption = SortOption.NO_FILTER
    private var allTasks: List<Task> = emptyList()
    private val filtersList = mutableListOf<TaskStatus>()

    init {
        activeTasksCount.postValue(0)
        isDataLoaded.postValue(false)
        isSearchActive.postValue(false)
        showFilterOptions.postValue(false)
        showSortOptions.postValue(false)
        dateText.postValue("Today")
        filtersList.apply {
            add(TaskStatus.ACTIVE)
            add(TaskStatus.WAITING)
            add(TaskStatus.COMPLETED)
        }
        // attach listeners
        ExecutePaymentsBaseViewModel.bindOnTasksStatusUpdatedListener(listener = this)
        TasksAdapter.bindOnTaskItemClickListener(listener = this)
    }

    val activeTaskActions: SingleLiveEvent<ActiveTaskActions> = SingleLiveEvent()
    val taskLoadingResults: SingleLiveEvent<TaskLoadingResults> = SingleLiveEvent()

    fun goBack() {
        showFilterOptions.postValue(false)
        showSortOptions.postValue(false)
        if (isSearchActive.value!!) {
            isSearchActive.postValue(false)
            // restore all tasks, search no longer active
            activeTasks.postValue(allTasks)
        } else {
            // go back to previous fragment
            activeTaskActions.postValue(ActiveTaskActions.GoBack)
        }
    }

    fun activeSearch() {
        showFilterOptions.postValue(false)
        showSortOptions.postValue(false)
        if (!isSearchActive.value!!) {
            isSearchActive.postValue(true)
            activeTaskActions.postValue(ActiveTaskActions.ShowSearchOptions)
        }
    }

    fun showMenu() {
        // show options menu
        activeTaskActions.postValue(ActiveTaskActions.ShowMenuOptions)
    }

    fun selectDate() {
        // show calender to select date from
        showSortOptions.postValue(false)
        showFilterOptions.postValue(false)
        activeTaskActions.postValue(ActiveTaskActions.ShowCalenderOptions)
    }

    fun filterTask() {
        // show task filter options
        showSortOptions.postValue(false)
        showFilterOptions.postValue(!showFilterOptions.value!!)
        activeTaskActions.postValue(ActiveTaskActions.ShowFilterOptions)
    }

    fun sortTasks() {
        // show task sort options
        showFilterOptions.postValue(false)
        showSortOptions.postValue(!showSortOptions.value!!)
        activeTaskActions.postValue(ActiveTaskActions.ShowSortOptions)
    }

    fun checkDataValidity() {
//        val lastFetch: Long = repo.lastFetchAt
//        val now: Long = Calendar.getInstance().time.time
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(now - lastFetch)
//        if (minutes > 15.toLong()) {
        if (isInternetAvailable(kodein.direct.instance())) {
            // get tasks from the server
            isDataLoaded.postValue(false)
            loadTasks()
        } else {
            activeTaskActions.postValue(ActiveTaskActions.InternetConnectionError)
            isDataLoaded.postValue(true)
        }
//        }
    }

    fun updateDate(dayOfMonth: Int, monthOfYear: Int, year: Int) {
        val date = DateUtils.getFormattedDate(
            day = dayOfMonth,
            month = monthOfYear,
            year = year,
            pattern = "d-MMM-YYYY"
        )
        addTasksWithStatus(date)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSplitTypeChanged(radioGroup: RadioGroup?, id: Int) {
        when (id) {
            R.id.rb_no_filter -> {
                sortOption = SortOption.NO_FILTER
            }
            R.id.rb_task_status -> {
                sortOption = SortOption.SORT_BY_STATUS
            }
            R.id.rb_date_assigned -> {
                sortOption = SortOption.SORT_BY_DATE
            }
        }
        addTasksWithStatus(enteredSearchQuery.value ?: "")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onActiveCheckedChanged(checkBox: CompoundButton, isCheck: Boolean) {
        if (isCheck) filtersList.add(TaskStatus.ACTIVE)
        else filtersList.remove(TaskStatus.ACTIVE)
        addTasksWithStatus(enteredSearchQuery.value ?: "")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onWaitingCheckedChanged(checkBox: CompoundButton, isCheck: Boolean) {
        if (isCheck) filtersList.add(TaskStatus.WAITING)
        else filtersList.remove(TaskStatus.WAITING)
        addTasksWithStatus(enteredSearchQuery.value ?: "")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onCompletedCheckedChanged(checkBox: CompoundButton, isCheck: Boolean) {
        if (isCheck) filtersList.add(TaskStatus.COMPLETED)
        else filtersList.remove(TaskStatus.COMPLETED)
        addTasksWithStatus(enteredSearchQuery.value ?: "")
    }

    private fun addTasksWithStatus(query: String) {
        mutableListOf<Task>().apply {
            for (e in allTasks)
                if (e.toString().toLowerCase(Locale.getDefault())
                        .contains(query.toLowerCase(Locale.getDefault()))
                    && filtersList.contains(e.taskStatus)
                )
                    add(e)
            // now sort the tasks as per sort
            when (sortOption) {
                SortOption.NO_FILTER -> {
                    Timber.d("Applying no sort..")
                }
                SortOption.SORT_BY_STATUS -> {
                    this.sortBy {
                        it.taskStatus
                    }
                }
                SortOption.SORT_BY_DATE -> {
                    this.sortByDescending {
                        it.date
                    }
                }
            }
            // post the sorted tasks
            activeTasks.postValue(this)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (text.isNotEmpty()) addTasksWithStatus(query = text.toString())
        else activeTasks.postValue(allTasks)
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
                        Timber.d("HTTP STATUS CODE : ACCEPTED  (202)")
                        // in case of successful task fetch
                        taskLoadingResults.postValue(TaskLoadingResults.LoadingFinished(isSuccessful = true))
                        val tasks = response.body()!!
                        Timber.d("Fetched Tasks: $tasks")
                        tasks.apply {
                            allTasks = this.results
                            activeTasks.postValue(this.results)
                            activeTasksCount.postValue(this.results.count())
                        }
                        updateLastFetch()
                        isDataNotLoaded = false
                    }

                    AppConstants.NOT_FOUND -> {
                        // in case of no internet connection
                        Timber.d("HTTP STATUS CODE : NOT_FOUND  (404)")
                        taskLoadingResults.postValue(TaskLoadingResults.LoadingFinished(isSuccessful = false))
                    }

                    AppConstants.UNAUTHORIZED -> {
                        // in case the login session has expired
                        Timber.d("Login session expired!")
                        repo.logoutUser()
                        taskLoadingResults.postValue(TaskLoadingResults.LoginSessionExpired)
                    }

                    else -> {
                        Timber.d("Got an unexpected response: ${response.code()}")
                        taskLoadingResults.postValue(TaskLoadingResults.LoadingFinished(isSuccessful = false))
                    }
                }
            }
        }
    }

    override fun onTaskItemClicked(action: TaskActions) {
        when (action) {
            is TaskActions.ExecuteTask -> {
                if (action.task.taskStatus == TaskStatus.ACTIVE)
                    activeTaskActions.postValue(ActiveTaskActions.ExecuteTask(action.task))
            }
        }
    }

    override fun onTaskStatusUpdatedListener(
        taskID: String,
        taskMarks: ExecutePaymentsBaseViewModel.TaskMarks
    ) {
        val tasks = activeTasks.value
        tasks?.let {
            val task = it.find { task ->
                task.taskID == taskID
            }!!
            task.taskStatus = when (taskMarks) {
                ExecutePaymentsBaseViewModel.TaskMarks.ADMIN_VERIFIED -> {
                    TaskStatus.WAITING
                }
                ExecutePaymentsBaseViewModel.TaskMarks.CLIENT_VERIFIED -> {
                    TaskStatus.COMPLETED
                }
            }
        }
    }

}