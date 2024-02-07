package com.ke.dawaati.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.ke.dawaati.R

class UserNameDrawable(private val initials: String, context: Context) : Drawable() {

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorWhite)
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.abc_text_size_display_1_material)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    override fun draw(canvas: Canvas) {
        val viewBounds = bounds

        canvas.drawCircle(
            viewBounds.exactCenterX(),
            viewBounds.exactCenterY(),
            viewBounds.height() / 2f,
            backgroundPaint
        )

        val fontMetrics = textPaint.fontMetrics

        canvas.drawText(
            initials,
            viewBounds.exactCenterX(),
            viewBounds.exactCenterY() - (fontMetrics.descent + fontMetrics.ascent) / 2,
            textPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.colorFilter = colorFilter
    }

    override fun getOpacity() = textPaint.alpha
}
