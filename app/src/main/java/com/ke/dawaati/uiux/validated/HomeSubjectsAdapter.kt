package com.ke.dawaati.uiux.validated

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.ke.dawaati.R
import com.ke.dawaati.databinding.ItemSubjectBinding
import com.ke.dawaati.db.model.SubjectsEntity
import com.ke.dawaati.uiux.MainActivity

class HomeSubjectsAdapter(
    private val subjectSelected: (SubjectsEntity) -> Unit
) : ListAdapter<SubjectsEntity, HomeSubjectsAdapter.HomeSubjectViewHolder>(DIFF_UTIL) {

    private var displayMetrics = DisplayMetrics()
    private var screenWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSubjectViewHolder {
        (parent.context as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels

        return HomeSubjectViewHolder(
            ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeSubjectViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class HomeSubjectViewHolder(private val binding: ItemSubjectBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(content: SubjectsEntity) {
            binding.apply {
                subjectTitle.text = content.name

                val imageID = when {
                    content.name.equals("Mathematics", ignoreCase = true) -> {
                        R.drawable.il_subject_mathematics
                    }
                    content.name.equals("English", ignoreCase = true) -> {
                        R.drawable.il_subject_english
                    }
                    content.name.equals("Physics", ignoreCase = true) -> {
                        R.drawable.il_subject_physics
                    }
                    content.name.equals("Chemistry", ignoreCase = true) -> {
                        R.drawable.il_subject_chemistry
                    }
                    content.name.equals("Biology", ignoreCase = true) -> {
                        R.drawable.il_subject_biology
                    }
                    content.name.equals("History", ignoreCase = true) -> {
                        R.drawable.il_subject_history
                    }
                    content.name.equals("Kiswahili", ignoreCase = true) -> {
                        R.drawable.il_subject_kiswahili
                    }
                    content.name.equals("Take Test", ignoreCase = true) -> {
                        R.drawable.il_take_test
                    }
                    content.name.equals("WhatsApp", ignoreCase = true) -> {
                        R.drawable.il_whatsapp
                    }
                    else -> {
                        R.drawable.ic_subject_biology
                    }
                }
                subjectImage.background = VectorDrawableCompat.create(subjectTitle.context.resources, imageID, null)

                /*val itemPadding = 8

                // here you may change the divide amount from 2.5 to whatever you need
                val itemWidth = (screenWidth - itemPadding).div(3)

                val layoutParams = itemView.layoutParams
                layoutParams.height = layoutParams.height
                layoutParams.width = itemWidth
                itemView.layoutParams = layoutParams*/

                itemView.setOnClickListener {
                    subjectSelected.invoke(content)
                }
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SubjectsEntity>() {
            override fun areItemsTheSame(
                oldItem: SubjectsEntity,
                newItem: SubjectsEntity
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SubjectsEntity,
                newItem: SubjectsEntity
            ): Boolean = oldItem == newItem
        }
    }
}
