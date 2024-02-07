package com.ke.dawaati.uiux.validated.content.evaluations

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.databinding.ItemEvaluationBinding

class ContentEvaluationsAdapter(
    private val selectedEvaluation: (DisplayEvaluations) -> Unit
) : RecyclerView.Adapter<ContentEvaluationsAdapter.ViewHolder>(), Filterable {

    private val items = mutableListOf<DisplayEvaluations>()

    private val originalModel = mutableListOf<DisplayEvaluations>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentEvaluationsAdapter.ViewHolder =
        ViewHolder(
            ItemEvaluationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ContentEvaluationsAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemEvaluationBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            itemView.setOnClickListener {
                selectedEvaluation(items[position])
            }
        }

        fun bind(content: DisplayEvaluations) {
            binding.apply {
                evaluationName.text = content.exam_name
                evaluationDuration.text = content.exam_duration
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setEvaluations(evaluationsEntity: List<DisplayEvaluations>) {
        items.clear()
        items.addAll(evaluationsEntity)
        originalModel.clear()
        originalModel.addAll(evaluationsEntity)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                items.clear()
                if (!constraint.isNullOrBlank() && constraint.length > 1) {
                    items.addAll(
                        originalModel.filter {
                            it.exam_name.contains(constraint, ignoreCase = true)
                        }
                    )
                } else {
                    items.addAll(originalModel)
                }
                return FilterResults().apply {
                    values = items
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}
