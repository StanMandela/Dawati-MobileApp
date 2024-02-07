package com.ke.dawaati.widgets.reader

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.MotionEvent
import com.ke.dawaati.R
import com.ke.dawaati.widgets.reader.adapter.PDFPagerAdapter
import com.ke.dawaati.widgets.reader.adapter.PdfScale

class PDFViewPagerZoom @JvmOverloads constructor(
    context: Context,
    pdfPath: String?,
    attrs: AttributeSet?
) : PDFViewPager(context, pdfPath, attrs) {

    override fun initAdapter(context: Context?, pdfPath: String?) {
        adapter = PDFPagerAdapter.Builder(context!!)
            .setPdfPath(pdfPath!!)
            .setOffScreenSize(offscreenPageLimit)
            .create()
    }

    override fun init(attrs: AttributeSet?) {
        if (isInEditMode) {
            setBackgroundResource(R.drawable.icon_file_pdf)
            return
        }
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PDFViewPager)
            val assetFileName = a.getString(R.styleable.PDFViewPager_assetFileName)
            val scale = a.getFloat(R.styleable.PDFViewPager_scale, PdfScale.DEFAULT_SCALE)
            if (assetFileName != null && assetFileName.isNotEmpty()) {
                adapter = PDFPagerAdapter.Builder(context)
                    .setPdfPath(assetFileName)
                    .setScale(scale)
                    .setOffScreenSize(offscreenPageLimit)
                    .create()
            }
            a.recycle()
        }
    }

    /**
     * Bugfix explained in https://github.com/chrisbanes/PhotoView
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
