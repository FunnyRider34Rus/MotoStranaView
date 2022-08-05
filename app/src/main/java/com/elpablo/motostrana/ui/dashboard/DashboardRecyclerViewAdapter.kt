package com.elpablo.motostrana.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.elpablo.motostrana.databinding.ItemEventBinding
import com.elpablo.motostrana.model.Event

class DashboardRecyclerViewAdapter: ListAdapter<Event, DashboardRecyclerViewAdapter.ItemHolder>(ItemDiffUtil()) {

    class ItemHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            with(binding){
                eventImage.load(event.image?.toUri())
                eventTitle.text = event.title
                eventDate.text = event.date
                eventText.text = event.textHeader
            }
        }
        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }

    class ItemDiffUtil: DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}