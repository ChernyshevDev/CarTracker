package dev.chernyshev.cartracker.utils.extensions

import android.util.DisplayMetrics
import android.util.TypedValue

fun Int.dpToPx(displayMetrics: DisplayMetrics): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    displayMetrics
)