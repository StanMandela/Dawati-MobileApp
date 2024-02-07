package com.ke.dawaati.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ke.dawaati.R
import com.ke.dawaati.databinding.ItemLoadingBannerStateBinding

class LoadingBannerState @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val itemBinding =
        ItemLoadingBannerStateBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.LoadingStateItem).apply {
                setupLottiePadding()
                setLoadingTitle(title = getString(R.styleable.LoadingStateItem_LoadingTitle).orEmpty())
                recycle()
            }
        }
    }

    private fun setupLottiePadding() {
        itemBinding.loading.setPadding(-50, -50, -50, -50)
    }

    private fun setLoadingTitle(title: String) {
        itemBinding.loadingTitle.text = title
    }
}
