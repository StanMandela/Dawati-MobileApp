package com.ke.dawaati.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.ke.dawaati.R
import com.ke.dawaati.databinding.ItemEmptyStateBinding

class EmptyState @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val itemBinding =
        ItemEmptyStateBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.EmptyStateItem).apply {
                setupLottiePadding()
                setEmptyTitle(title = getString(R.styleable.EmptyStateItem_EmptyTitle).orEmpty())
                setEmptySubTitle(subTitle = getString(R.styleable.EmptyStateItem_EmptySubTitle).orEmpty())
                setImageVisible(
                    isAvailable = getBoolean(R.styleable.EmptyStateItem_ImageVisible, true)
                )
                recycle()
            }
        }
    }

    private fun setupLottiePadding() {
        itemBinding.loading.setPadding(-50, -50, -50, -50)
    }

    private fun setImageVisible(isAvailable: Boolean) {
        itemBinding.loading.isVisible = isAvailable
    }

    fun setEmptyTitle(title: String) {
        itemBinding.emptyTripsTitle.text = title
    }

    fun setEmptySubTitle(subTitle: String) {
        itemBinding.emptySubTitle.text = subTitle
    }
}
