package com.ke.dawaati.widgets.reader

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.ke.dawaati.R
import com.ke.dawaati.widgets.reader.adapter.PDFPagerAdapter

open class PDFViewPager @JvmOverloads constructor(
    context: Context,
    pdfPath: String?,
    attrs: AttributeSet?
) : ViewPager(context, attrs) {

    init {
        initAdapter(context, pdfPath)
    }

    protected open fun init(attrs: AttributeSet?) {
        if (isInEditMode) {
            setBackgroundResource(R.drawable.icon_file_pdf)
            return
        }
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PDFViewPager)
            val assetFileName = a.getString(R.styleable.PDFViewPager_assetFileName)
            if (assetFileName != null && assetFileName.isNotEmpty()) {
                initAdapter(context, assetFileName)
            }
            a.recycle()
        }
    }

    protected open fun initAdapter(context: Context?, pdfPath: String?) {
        adapter = PDFPagerAdapter.Builder(context!!)
            .setPdfPath(pdfPath!!)
            .setOffScreenSize(offscreenPageLimit)
            .create()
    }

    /**
     * PDFViewPager uses PhotoView, so this bugfix should be added
     * Issue explained in https://github.com/chrisbanes/PhotoView
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }
}
