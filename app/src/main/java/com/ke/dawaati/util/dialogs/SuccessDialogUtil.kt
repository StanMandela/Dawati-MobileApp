package com.ke.dawaati.util.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.ke.dawaati.R
import java.util.Objects

fun Dialog.success(
    title: String? = null,
    message: String? = null,
    dismissCallBack: (() -> Unit)? = null
) {
    setCanceledOnTouchOutside(false)
    setCancelable(true)
    setContentView(R.layout.dialog_success)
    val window: Window = Objects.requireNonNull<Window>(window).also {
        it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        it.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    val successTitle: TextView = findViewById(R.id.successTitle)
    val successMessage: TextView = findViewById(R.id.successMessage)
    val successDismissAction: TextView = findViewById(R.id.successDismissAction)

    title?.let { successTitle.text = it }
    message?.let { successMessage.text = it }
    successDismissAction.setOnClickListener {
        dismiss()
        dismissCallBack?.invoke()
    }
    show()
}
