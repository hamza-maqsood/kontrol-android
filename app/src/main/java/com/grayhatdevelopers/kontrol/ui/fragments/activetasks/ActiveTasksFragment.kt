package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.adapters.tasks.TasksAdapter
import com.grayhatdevelopers.kontrol.databinding.FragmentActiveTasksBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.ui.dialogs.Dialog
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.toast
import timber.log.Timber
import java.util.*

class ActiveTasksFragment : BaseFragment() {

    private val mViewModel: ActiveTasksViewModel by kodeinViewModel()
    private lateinit var activeTasksFragmentBinding: FragmentActiveTasksBinding
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activeTasksFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_active_tasks, // todo: swipe to refresh
            container,
            false
        )
        activeTasksFragmentBinding.apply {
            lifecycleOwner = this@ActiveTasksFragment
            viewModel = mViewModel
        }
        return activeTasksFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeActiveTasksActions()
        observeTaskLoadingResults()
        checkTasksValidity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateRecyclerView()
    }

    private fun checkTasksValidity() {
        mViewModel.checkDataValidity()
    }

    private fun logoutRider() {
        Dialog(requireContext(), Dialog.DialogType.ERROR).apply {
            title = "Session Expired!"
            message = "Your login session has expired. \nLogin again."
            positiveButtonText = "Logout"
            setCancelable(false)
            setOnPositiveButtonClickedListener(object : Dialog.OnPositiveButtonClicked {
                override fun onPositiveButtonClicked() {
                    dismiss()
                    navigateTo(ActiveTasksFragmentDirections.actionActiveTasksFragmentToLoginFragment())
                }
            })
            show()
        }
    }

    private fun observeTaskLoadingResults() {
        mViewModel.taskLoadingResults.observe(viewLifecycleOwner) {
            when (it) {
                TaskLoadingResults.LoadingTasks -> {
                    Timber.d("Task loading started")
                }
                TaskLoadingResults.LoginSessionExpired -> {
                    Timber.d("Rider login session has been expired!")
                    logoutRider()
                }

                is TaskLoadingResults.LoadingFinished -> {
                    if (it.isSuccessful) {
                        // tasks were loaded successfully
                        Timber.d("Task loaded successfully")
                    } else {
                        Timber.d("Task loading started")
                        context?.toast("Make sure you've an active internet connection!")
                    }
                }
            }
        }
    }

    private fun populateRecyclerView() {
        tasksAdapter = TasksAdapter(
            requireContext(),
            this@ActiveTasksFragment
        )
        activeTasksFragmentBinding.tasksList.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(context)
        }

        tasksAdapter.apply {
            // observe data changes in the list
            mViewModel.activeTasks.observe(viewLifecycleOwner) {
                submitList(it)
            }
        }
    }

    private fun observeActiveTasksActions() {
        mViewModel.activeTaskActions.observe(viewLifecycleOwner) {
            when (it) {
                ActiveTaskActions.ShowSortOptions -> {
                    Timber.d("Show Sort Clicked")
                }
                ActiveTaskActions.ShowFilterOptions -> {
                    Timber.d("Show Filter Options Clicked")
                }
                ActiveTaskActions.ShowCalenderOptions -> {
                    Timber.d("Show Calender Clicked")
                    val c = Calendar.getInstance()
                    val selectedYear = c.get(Calendar.YEAR)
                    val selectedMonth = c.get(Calendar.MONTH)
                    val selectedDay = c.get(Calendar.DAY_OF_MONTH)
                    DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        mViewModel.updateDate(dayOfMonth, monthOfYear, year)
                    }, selectedYear, selectedMonth, selectedDay).apply {
                        datePicker.maxDate = System.currentTimeMillis()
                        show()
                    }
                }
                ActiveTaskActions.ShowSearchOptions -> {
                    Timber.d("Active Search Clicked")
                }
                ActiveTaskActions.ShowMenuOptions -> {
                    Timber.d("Show Menu Options Clicked")
                }
                ActiveTaskActions.GoBack -> {
                    Timber.d("Go Back Clicked")
                    popBackToPrevious()
                }
                is ActiveTaskActions.ExecuteTask -> {
                    Timber.d("Execute Task Clicked")
                    val bundle = bundleOf("task" to it.task)
                    val direction =
                        ActiveTasksFragmentDirections.actionActiveTasksFragmentToExecuteTaskFragment(
                            it.task
                        )
                    navigateTo(direction, bundle)
                }
                ActiveTaskActions.InternetConnectionError -> {
                    context?.toast("Make sure you've a stable internet connection!")
                }
            }
        }
    }
}