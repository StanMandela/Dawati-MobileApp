package com.ke.dawaati.uiux.validated.subscribe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.databinding.ItemSubscriptionTypeBinding
import com.ke.dawaati.db.model.SubscriptionTypesEntity

class SubscribeAdapter(private val selectedOption: (SubscriptionTypesEntity) -> Unit) : ListAdapter<SubscriptionTypesEntity, SubscribeAdapter.SubscribeViewHolder>(DIFF_UTIL) {

    private var selectedSubscriptionOption = ""
    private var selectedOptionPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SubscribeViewHolder(
            ItemSubscriptionTypeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: SubscribeViewHolder, position: Int) {
        holder.bind(content = currentList[position], position = position)
    }

    inner class SubscribeViewHolder(private val binding: ItemSubscriptionTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: SubscriptionTypesEntity, position: Int) {
            binding.apply {
                subscriptionTypeName.text = content.title
                subscriptionTypeValue.text = "${content.amount}  Ksh"

                if (selectedSubscriptionOption.equals(content.title, ignoreCase = true)) {
                    subscriptionRadio.isChecked = true
                    selectedOptionPosition = position
                } else {
                    subscriptionRadio.isChecked = false
                }

                itemView.setOnClickListener {
                    selectedSubscriptionOption = content.title
                    selectedOption.invoke(content)
                    subscriptionRadio.isChecked = selectedSubscriptionOption.equals(content.title, ignoreCase = true)
                    notifyItemChanged(selectedOptionPosition)
                    selectedOptionPosition = position
                }
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SubscriptionTypesEntity>() {
            override fun areItemsTheSame(
                oldItem: SubscriptionTypesEntity,
                newItem: SubscriptionTypesEntity
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SubscriptionTypesEntity,
                newItem: SubscriptionTypesEntity
            ): Boolean = oldItem == newItem
        }
    }
}
