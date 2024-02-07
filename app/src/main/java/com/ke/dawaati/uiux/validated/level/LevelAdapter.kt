package com.ke.dawaati.uiux.validated.level

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.databinding.ItemFormSelectBinding
import com.ke.dawaati.db.model.LevelsEntity

class LevelAdapter(
    private val levelSelected: (LevelsEntity?) -> Unit
) : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

    private val items = mutableListOf<LevelsEntity>()

    private var selectedLevel: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelAdapter.ViewHolder =
        ViewHolder(
            ItemFormSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: LevelAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemFormSelectBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            itemView.setOnClickListener {
                when {
                    selectedLevel.isEmpty() -> {
                        selectedLevel = items[adapterPosition].level_code
                        levelSelected.invoke(items[adapterPosition])
                    }
                    selectedLevel.equals(items[adapterPosition].level_code, ignoreCase = true) -> {
                        selectedLevel = ""
                    }
                    else -> {
                        selectedLevel = items[adapterPosition].level_code
                        levelSelected.invoke(items[adapterPosition])
                    }
                }
                notifyDataSetChanged()
            }
        }

        fun bind(content: LevelsEntity) {
            binding.apply {
                schoolName.text = content.level_name
                schoolRadio.isChecked = (selectedLevel.isNotEmpty() && selectedLevel.equals(items[adapterPosition].level_code, ignoreCase = true))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setLevel(levelEntity: List<LevelsEntity>, selectedLevel: String) {
        this.items.apply {
            clear()
            addAll(levelEntity)
        }
        this.selectedLevel = selectedLevel
        notifyDataSetChanged()
    }
}
