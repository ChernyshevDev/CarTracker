package dev.chernyshev.cartracker.utils.extensions

import androidx.fragment.app.Fragment

val Fragment.displayHeightPixels: Int
    get() {
        return requireContext().resources.displayMetrics.heightPixels
    }