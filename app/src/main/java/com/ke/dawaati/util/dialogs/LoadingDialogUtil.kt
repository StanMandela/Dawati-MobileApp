package com.ke.dawaati.util.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import com.ke.dawaati.R
import java.util.Objects

fun Dialog.loader() {
    setCanceledOnTouchOutside(false)
    setCancelable(true)
    setContentView(R.layout.dialog_loader)
    val window: Window = Objects.requireNonNull<Window>(window).also {
        it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        it.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    val loader: LottieAnimationView = findViewById(R.id.loader)
    loader.setPadding(-10, -10, -10, -10)
    show()
}
