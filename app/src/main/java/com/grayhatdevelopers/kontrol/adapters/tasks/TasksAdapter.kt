package com.grayhatdevelopers.kontrol.adapters.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.adapters.DataBoundListAdapter
import com.grayhatdevelopers.kontrol.databinding.ItemActiveTaskBinding
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.models.task.TaskActions
import com.grayhatdevelopers.kontrol.models.task.TasksViewModel
import com.grayhatdevelopers.kontrol.models.task.TasksViewModelFactory
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber

class TasksAdapter(
    context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : DataBoundListAdapter<Task>(
    diffCallback = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }, context = context
) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_active_task,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: Task, position: Int) {
        when (binding) {
            is ItemActiveTaskBinding -> {
                binding.task = item
                binding.position = position + 1
                binding.viewModel = TasksViewModelFactory(kodein.direct.instance()).create(TasksViewModel::class.java)
                (binding.viewModel as TasksViewModel).taskActions.observe(viewLifecycleOwner) {
                    when (it) {
                        is TaskActions.ExecuteTask -> {
                            Timber.d("Executing task: $item")
                            // notify execute task
                            mOnTaskItemClickedListener?.onTaskItemClicked(it)
                        }
                    }
                }
            }
        }
    }

    interface OnTaskItemClickedListener {
        fun onTaskItemClicked(action: TaskActions)
    }


    companion object {

        private var mOnTaskItemClickedListener: OnTaskItemClickedListener? = null
        fun bindOnTaskItemClickListener(listener: OnTaskItemClickedListener) {
            mOnTaskItemClickedListener = listener
        }
    }
}