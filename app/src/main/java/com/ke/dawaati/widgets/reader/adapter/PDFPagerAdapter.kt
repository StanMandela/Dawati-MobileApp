package com.ke.dawaati.widgets.reader.adapter

import android.content.Context
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.ke.dawaati.R
import com.ke.dawaati.widgets.reader.util.EmptyClickListener
import com.ke.dawaati.widgets.reader.util.PhotoViewAttacher
import java.lang.ref.WeakReference

class PDFPagerAdapter(context: Context, pdfPath: String) : BasePDFPagerAdapter(context, pdfPath), PhotoViewAttacher.OnMatrixChangedListener {
    var attachers: SparseArray<WeakReference<PhotoViewAttacher>>?
    var scale = PdfScale()
    var pageClickListener: View.OnClickListener = EmptyClickListener()
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v: View = inflater!!.inflate(R.layout.view_pdf_page, container, false)
        val iv = v.findViewById<ImageView>(R.id.imageView)
        if (renderer == null || count < position) {
            return v
        }
        val page = getPDFPage(renderer!!, position)
        val bitmap = bitmapContainer!![position]
        page.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        val attacher = PhotoViewAttacher(iv)
        attacher.setScale(scale.scale, scale.centerX, scale.centerY, true)
        attacher.setOnMatrixChangeListener(this)
        attachers!!.put(position, WeakReference<PhotoViewAttacher>(attacher))
        iv.setImageBitmap(bitmap)
        attacher.onPhotoTapListener = object : PhotoViewAttacher.OnPhotoTapListener {
            override fun onPhotoTap(view: View?, x: Float, y: Float) {
                pageClickListener.onClick(view)
            }
        }
        attacher.update()
        (container as ViewPager).addView(v, 0)
        return v
    }

    override fun close() {
        super.close()
        if (attachers != null) {
            attachers!!.clear()
            attachers = null
        }
    }

    override fun onMatrixChanged(rect: RectF?) {
        if (scale.scale != PdfScale.Companion.DEFAULT_SCALE) {
//            scale.setCenterX(rect.centerX());
//            scale.setCenterY(rect.centerY());
        }
    }

    class Builder(var context: Context) {
        var pdfPath = ""
        var scale = DEFAULT_SCALE
        var centerX = 0f
        var centerY = 0f
        var offScreenSize: Int = DEFAULT_OFFSCREENSIZE
        var renderQuality: Float = DEFAULT_QUALITY
        var pageClickListener: View.OnClickListener = EmptyClickListener()
        fun setScale(scale: Float): Builder {
            this.scale = scale
            return this
        }

        fun setScale(scale: PdfScale): Builder {
            this.scale = scale.scale
            centerX = scale.centerX
            centerY = scale.centerY
            return this
        }

        fun setCenterX(centerX: Float): Builder {
            this.centerX = centerX
            return this
        }

        fun setCenterY(centerY: Float): Builder {
            this.centerY = centerY
            return this
        }

        fun setRenderQuality(renderQuality: Float): Builder {
            this.renderQuality = renderQuality
            return this
        }

        fun setOffScreenSize(offScreenSize: Int): Builder {
            this.offScreenSize = offScreenSize
            return this
        }

        fun setPdfPath(path: String): Builder {
            pdfPath = path
            return this
        }

        fun setOnPageClickListener(listener: View.OnClickListener?): Builder {
            if (listener != null) {
                pageClickListener = listener
            }
            return this
        }

        fun create(): PDFPagerAdapter {
            val adapter = PDFPagerAdapter(context, pdfPath)
            adapter.scale.scale = scale
            adapter.scale.centerX = centerX
            adapter.scale.centerY = centerY
            adapter.offScreenSize = offScreenSize
            adapter.renderQuality = renderQuality
            adapter.pageClickListener = pageClickListener
            return adapter
        }
    }

    companion object {
        private const val DEFAULT_SCALE = 1f
    }

    init {
        attachers = SparseArray<WeakReference<PhotoViewAttacher>>()
    }
}
