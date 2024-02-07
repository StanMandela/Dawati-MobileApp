package com.ke.dawaati.widgets.reader.adapter

import android.app.Activity
import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ke.dawaati.R
import java.io.File
import java.io.IOException
import java.net.URI

open class BasePDFPagerAdapter : PagerAdapter {
    var pdfPath: String
    var context: Context
    var renderer: PdfRenderer? = null
    var bitmapContainer: BitmapContainer? = null
    var inflater: LayoutInflater? = null
    protected var renderQuality: Float
    protected var offScreenSize: Int

    constructor(context: Context, pdfPath: String) {
        this.pdfPath = pdfPath
        this.context = context
        renderQuality = DEFAULT_QUALITY
        offScreenSize = DEFAULT_OFFSCREENSIZE
        init()
    }

    /**
     * This constructor was added for those who want to customize ViewPager's offScreenSize attr
     */
    constructor(context: Context, pdfPath: String, offScreenSize: Int) {
        this.pdfPath = pdfPath
        this.context = context
        renderQuality = DEFAULT_QUALITY
        this.offScreenSize = offScreenSize
        init()
    }

    protected fun init() {
        try {
            renderer = PdfRenderer(getSeekableFileDescriptor(pdfPath)!!)
            inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val params = extractPdfParamsFromFirstPage(renderer!!, renderQuality)
            bitmapContainer = SimpleBitmapPool(params)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun extractPdfParamsFromFirstPage(renderer: PdfRenderer, renderQuality: Float): PdfRendererParams {
        val samplePage = getPDFPage(renderer, FIRST_PAGE)
        val params = PdfRendererParams()
        params.renderQuality = renderQuality
        params.offScreenSize = offScreenSize
        params.width = (samplePage.width * renderQuality).toInt()
        params.height = (samplePage.height * renderQuality).toInt()
        samplePage.close()
        return params
    }

    @Throws(IOException::class)
    protected fun getSeekableFileDescriptor(path: String): ParcelFileDescriptor? {
        val parcelFileDescriptor: ParcelFileDescriptor?
        var pdfCopy = File(path)
        if (pdfCopy.exists()) {
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY)
            return parcelFileDescriptor
        }
        if (isAnAsset(path)) {
            pdfCopy = File(context.cacheDir, path)
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY)
        } else {
            val uri = URI.create(String.format("file://%s", path))
            parcelFileDescriptor = context.contentResolver.openFileDescriptor(Uri.parse(uri.toString()), "rw")
        }
        return parcelFileDescriptor
    }

    private fun isAnAsset(path: String): Boolean {
        return !path.startsWith("/")
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v: View = inflater!!.inflate(R.layout.view_pdf_page, container, false)
        val iv = v.findViewById<View>(R.id.imageView) as ImageView
        if (renderer == null || count < position) {
            return v
        }
        val page = getPDFPage(renderer!!, position)
        val bitmap = bitmapContainer!![position]
        page.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        iv.setImageBitmap(bitmap)
        (container as ViewPager).addView(v, 0)
        return v
    }

    protected fun getPDFPage(renderer: PdfRenderer, position: Int): PdfRenderer.Page {
        return renderer.openPage(position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // bitmap.recycle() causes crashes if called here.
        // All bitmaps are recycled in close().
    }

    open fun close() {
        releaseAllBitmaps()
        if (renderer != null) {
            renderer!!.close()
        }
    }

    protected fun releaseAllBitmaps() {
        if (bitmapContainer != null) {
            bitmapContainer!!.clear()
        }
    }

    override fun getCount(): Int {
        return if (renderer != null) renderer!!.pageCount else 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    companion object {
        const val FIRST_PAGE = 0
        const val DEFAULT_QUALITY = 2.0f
        const val DEFAULT_OFFSCREENSIZE = 1
    }
}
