package com.ke.dawaati.uiux.authenticate.school

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.databinding.ItemSchoolPickerBinding
import com.ke.dawaati.db.model.SchoolEntity

class SchoolAdapter(
    private val schoolSelected: (SchoolEntity) -> Unit,
    schoolID: String = ""
) : RecyclerView.Adapter<SchoolAdapter.ViewHolder>(), Filterable {

    private var selectedSchoolID = schoolID
    private val items = mutableListOf<SchoolEntity>()
    private val originalModel = mutableListOf<SchoolEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemSchoolPickerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: SchoolAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemSchoolPickerBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(content: SchoolEntity) {
            binding.apply {
                schoolName.text = content.schoolName
                schoolRadio.isChecked = selectedSchoolID.equals(content.schoolID, ignoreCase = true)

                itemView.setOnClickListener {
                    selectedSchoolID = content.schoolID
                    schoolSelected.invoke(content)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(schools: List<SchoolEntity>) {
        items.apply {
            clear()
            addAll(schools)
        }
        originalModel.apply {
            clear()
            addAll(schools)
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                items.clear()
                if (!constraint.isNullOrBlank() && constraint.length > 1) {
                    items.addAll(
                        originalModel.filter {
                            it.schoolName.contains(constraint, ignoreCase = true)
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
