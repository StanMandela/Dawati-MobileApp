package com.ke.dawaati.uiux.validated

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ke.dawaati.databinding.ItemVideoResumeBinding
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoSubTopics

class HomeVideosAdapter(
    private val videoSelectedSelected: (DisplayVideoSubTopics) -> Unit
) : ListAdapter<DisplayVideoSubTopics, HomeVideosAdapter.HomeVideosViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeVideosViewHolder {
        return HomeVideosViewHolder(
            ItemVideoResumeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeVideosViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class HomeVideosViewHolder(
        val binding: ItemVideoResumeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DisplayVideoSubTopics) {
            binding.apply {
                Glide
                    .with(subjectImage.context)
                    .load(item.file_url + item.file_name.replace(" ", "%20"))
                    .into(subjectImage)

                videoTitle.apply {
                    text = item.name
                }

                videoSubject.apply {
                    text = item.subject
                }

                itemView.setOnClickListener {
                    videoSelectedSelected(item)
                }
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<DisplayVideoSubTopics>() {
            override fun areItemsTheSame(
                oldItem: DisplayVideoSubTopics,
                newItem: DisplayVideoSubTopics
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: DisplayVideoSubTopics,
                newItem: DisplayVideoSubTopics
            ) = oldItem == newItem
        }
    }
}
