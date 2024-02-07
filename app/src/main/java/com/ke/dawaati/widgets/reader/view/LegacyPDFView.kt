package com.ke.dawaati.widgets.reader.view

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.ke.dawaati.R
import com.ke.dawaati.widgets.reader.remote.DownloadFile
import com.ke.dawaati.widgets.reader.remote.DownloadFileUrlConnectionImpl

open class LegacyPDFView : LinearLayout, DownloadFile.Listener {
    private var textView: TextView? = null
    private var progressBar: ProgressBar? = null
    protected var button: Button? = null
    var downloadFile: DownloadFile? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, downloadFile: DownloadFile?) : super(context) {
        this.downloadFile = downloadFile
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val v = inflate(context, layoutId, this)
        if (viewFound(v, R.id.legacy_pdf_textView)) {
            textView = v.findViewById(R.id.legacy_pdf_textView)
        }
        if (viewFound(v, R.id.legacy_pdf_button)) {
            button = v.findViewById(R.id.legacy_pdf_button)
        }
        if (viewFound(v, R.id.legacy_pdf_progressBar)) {
            progressBar = v.findViewById(R.id.legacy_pdf_progressBar)
        }
        if (downloadFile == null) {
            downloadFile = DownloadFileUrlConnectionImpl(context, Handler(), this)
        }
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.LegacyPDFView)
            a.recycle()
        }
    }

    private fun viewFound(root: View, id: Int): Boolean {
        return root.findViewById<View?>(id) != null
    }

    override fun setOnClickListener(l: OnClickListener?) {
        button!!.setOnClickListener(l)
    }

    var max: Int
        get() = progressBar!!.max
        set(max) {
            progressBar!!.max = max
        }

    fun setProgress(progress: Int) {
        progressBar!!.progress = progress
    }

    fun setText(text: String?) {
        textView!!.text = text
    }

    fun setText(resId: Int) {
        textView!!.setText(resId)
    }

    // region overridable methods - You can customize this view by adding a subclass with your own implementations
    protected val layoutId: Int
        protected get() = R.layout.view_legacy_pdf

    // end region
    // region DownloadFile.Listener virtuals
    override fun onSuccess(url: String?, destinationPath: String?) {}
    override fun onFailure(e: Exception?) {}
    override fun onProgressUpdate(progress: Int, total: Int) {
        if (max != total) {
            max = total
        }
        setProgress(progress)
    } // endregion
}
