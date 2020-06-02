package com.grayhatdevelopers.kontrol.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein


abstract class DataBoundListAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    context: Context
) : ListAdapter<T, DataBoundViewHolder>(
    AsyncDifferConfig.Builder<T>(diffCallback)
        .build()
), KodeinAware {

    override val kodein: Kodein by closestKodein(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val binding = createBinding(parent, viewType)
        val viewHolder = DataBoundViewHolder(binding)
        binding.lifecycleOwner = viewHolder
        return viewHolder
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: DataBoundViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

    protected abstract fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        if (position < itemCount) {
            bind(holder.binding, getItem(position), position)
            holder.binding.executePendingBindings()
        }
    }

    protected abstract fun bind(binding: ViewDataBinding, item: T, position: Int)
}