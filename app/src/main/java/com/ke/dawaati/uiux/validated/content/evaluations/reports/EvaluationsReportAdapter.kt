package com.ke.dawaati.uiux.validated.content.evaluations.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.databinding.ItemEvaluationAttemptsBinding
import com.ke.dawaati.db.model.EvaluationsAttemptsEntity
import com.ke.dawaati.util.toTitleCase

class EvaluationsReportAdapter(
    private val selectedEvaluation: (EvaluationsAttemptsEntity) -> Unit
) : RecyclerView.Adapter<EvaluationsReportAdapter.ViewHolder>() {

    private val items = mutableListOf<EvaluationsAttemptsEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationsReportAdapter.ViewHolder =
        ViewHolder(
            ItemEvaluationAttemptsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: EvaluationsReportAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemEvaluationAttemptsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                selectedEvaluation(items[position])
            }
        }

        fun bind(content: EvaluationsAttemptsEntity) {
            binding.apply {
                evaluationTitle.text = toTitleCase(str = content.exam_name, handleWhiteSpace = true)
                evaluationSubject.text = content.subject
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setEvaluations(evaluationsEntity: List<EvaluationsAttemptsEntity>) {
        items.clear()
        items.addAll(evaluationsEntity)
        notifyDataSetChanged()
    }
}
