package com.grayhatdevelopers.kontrol.ui.fragments.taskshistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentTasksHistoryBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import timber.log.Timber

class TasksHistoryFragment : BaseFragment() {

    private lateinit var fragmentTasksHistoryBinding: FragmentTasksHistoryBinding
    private val mViewModel: TasksHistoryViewModel by kodeinViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTasksHistoryBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tasks_history,
            container,
            false
        )
        fragmentTasksHistoryBinding.apply {
            lifecycleOwner = this@TasksHistoryFragment
            viewModel = mViewModel
        }
        return fragmentTasksHistoryBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeTasksHistoryActions()
    }

    private fun observeTasksHistoryActions() {
        mViewModel.tasksHistoryActions.observe(viewLifecycleOwner) {
            when (it) {
                TasksHistoryActions.GoBack -> {
                    Timber.d("Going Back")
                    popBackToPrevious()
                }
            }
        }
    }
}
