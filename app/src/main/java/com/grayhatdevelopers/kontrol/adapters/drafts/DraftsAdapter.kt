package com.grayhatdevelopers.kontrol.adapters.drafts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.adapters.DataBoundListAdapter
import com.grayhatdevelopers.kontrol.databinding.ItemActiveTaskBinding
import com.grayhatdevelopers.kontrol.databinding.ItemDateBinding
import com.grayhatdevelopers.kontrol.models.drafts.Draft
import com.grayhatdevelopers.kontrol.utils.DateUtils

class DraftsAdapter (
    context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : DataBoundListAdapter<Draft>(
    diffCallback = object : DiffUtil.ItemCallback<Draft>() {
        override fun areItemsTheSame(oldItem: Draft, newItem: Draft): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Draft, newItem: Draft): Boolean {
            return oldItem == newItem
        }
    }, context = context
) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            when (viewType) {
                R.layout.item_date -> R.layout.item_date
                R.layout.item_active_task -> R.layout.item_active_task
                else -> throw IllegalArgumentException("Unexpected viewType")
            },
            parent,
            false
        )
    }

    override fun getItemViewType(position: Int): Int =
        if (currentList[position].isDate) DraftsViewItem.DateView.resource
        else DraftsViewItem.DraftView.resource

    override fun bind(binding: ViewDataBinding, item: Draft, position: Int) {
        binding.lifecycleOwner = viewLifecycleOwner
        when (binding) {
            is ItemDateBinding -> {
                when (item.date) {
                    DateUtils.getToday() -> {
                        // today
                        binding.date = "Today"
                    }
                    DateUtils.getYesterday() -> {
                        // yesterday
                        binding.date = "Yesterday"
                    }
                    else -> {
                        // some random date
                        binding.date = item.date
                    }
                }
            }

            is ItemActiveTaskBinding -> {
                binding.task = item.task
                // todo here: add a view model, handle onClick
            }
        }
    }
}