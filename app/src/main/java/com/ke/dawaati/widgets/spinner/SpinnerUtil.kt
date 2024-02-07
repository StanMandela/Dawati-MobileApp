package com.ke.dawaati.widgets.spinner

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import java.util.regex.Pattern
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun isLight(color: Int): Boolean {
    return sqrt(
        Color.red(color) * Color.red(color) * .241 + Color.green(color) * Color.green(color) * .691 + Color.blue(
            color
        ) * Color.blue(color) * .068
    ) > 130
}

fun dp2px(context: Context, dp: Float): Int {
    val r = context.resources
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
    return px.roundToInt()
}

abstract class METLengthChecker {
    abstract fun getLength(text: CharSequence?): Int
}

/**
 * Base Validator class to either implement or inherit from for custom validation
 */
abstract class METValidator internal constructor(
    /**
     * Error message that the view will display if validation fails.
     *
     *
     * This is protected, so you can change this dynamically in your [.isValid]
     * implementation. If necessary, you can also interact with this via its getter and setter.
     */
    var errorMessage: String
) {

    /**
     * Abstract method to implement your own validation checking.
     *
     * @param text The CharSequence representation of the text in the EditText field. Cannot be null, but may be empty.
     * @param isEmpty Boolean indicating whether or not the text param is empty
     * @return True if valid, false if not
     */
    abstract fun isValid(text: CharSequence, isEmpty: Boolean): Boolean
}

/**
 * Custom validator for Regexes
 */
class RegexpValidator : METValidator {
    private var pattern: Pattern

    constructor(errorMessage: String, regex: String) : super(errorMessage) {
        pattern = Pattern.compile(regex)
    }

    constructor(errorMessage: String, pattern: Pattern) : super(errorMessage) {
        this.pattern = pattern
    }

    override fun isValid(text: CharSequence, isEmpty: Boolean): Boolean {
        return pattern.matcher(text).matches()
    }
}
