package com.grayhatdevelopers.kontrol.ui.fragments.activetasks

import android.os.Bundle
import android.util.Log
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
import com.grayhatdevelopers.kontrol.repo.Repository
import com.grayhatdevelopers.kontrol.ui.dialogs.Dialog
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.InjectorUtils
import com.grayhatdevelopers.kontrol.utils.toast

class ActiveTasksFragment : BaseFragment() {

    private lateinit var mViewModel: ActiveTasksViewModel
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
            mViewModel = InjectorUtils.provideActiveTasksViewModel()
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
        Dialog(context!!, Dialog.DialogType.ERROR).apply {
            title = "Session Expired!"
            message = "Your login session has expired. \nLogin again."
            positiveButtonText = "Logout"
            setCancelable(false)
            setOnPositiveButtonClickedListener(object : Dialog.OnPositiveButtonClicked {
                override fun onPositiveButtonClicked() {
                    dismiss()
                    baseViewModel.navigate(ActiveTasksFragmentDirections.actionActiveTasksFragmentToLoginFragment())
                }
            })
            show()
        }
    }

    private fun observeTaskLoadingResults() {
        mViewModel.taskLoadingResults.observe(viewLifecycleOwner) {
            when (it) {
                TaskLoadingResults.LoadingTasks -> {
                    Log.d(TAG, "Task loading started")
                }
                TaskLoadingResults.LoginSessionExpired -> {
                    Log.d(TAG, "Rider login session has been expired!")
                    logoutRider()
                }

                is TaskLoadingResults.LoadingFinished -> {
                    if (it.isSuccessful) {
                        // tasks were loaded successfully
                        Log.d(TAG, "Task loaded successfully")
                    } else {
                        Log.d(TAG, "Task loading started")
                        context?.toast("Make sure you've an active internet connection!")
                    }
                }
            }
        }
    }

    private fun populateRecyclerView() {
        tasksAdapter = TasksAdapter(
            this@ActiveTasksFragment
        )
        activeTasksFragmentBinding.tasksList.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(context)
        }

        tasksAdapter.apply {
            // observe data changes in the list
            Repository.getInstance().tasks.observe(viewLifecycleOwner) {
                this.submitList(it)
            }
        }
    }

    private fun observeActiveTasksActions() {
        mViewModel.activeTaskActions.observe(viewLifecycleOwner) {
            when (it) {
                ActiveTaskActions.ShowSortOptions -> {
                    Log.d(TAG, "Show Sort Clicked")
                }
                ActiveTaskActions.ShowFilterOptions -> {
                    Log.d(TAG, "Show Filter Options Clicked")
                }
                ActiveTaskActions.ShowCalenderOptions -> {
                    Log.d(TAG, "Show Calender Clicked")
                }
                ActiveTaskActions.ShowSearchOptions -> {
                    Log.d(TAG, "Active Search Clicked")
                }
                ActiveTaskActions.ShowMenuOptions -> {
                    Log.d(TAG, "Show Menu Options Clicked")
                }
                ActiveTaskActions.GoBack -> {
                    Log.d(TAG, "Go Back Clicked")
                    baseViewModel.popBack()
                }
                is ActiveTaskActions.ExecuteTask -> {
                    Log.d(TAG, "Execute Task Clicked")
                    val bundle = bundleOf("task" to it.task)
                    val direction =
                        ActiveTasksFragmentDirections.actionActiveTasksFragmentToExecuteTaskFragment(
                            it.task
                        )
                    baseViewModel.navigate(direction, bundle)
                }
            }
        }
    }

    companion object {
        private const val TAG = "ActiveTasksFragment"
    }
}