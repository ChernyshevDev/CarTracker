package dev.chernyshev.cartracker.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import dev.chernyshev.cartracker.R
import java.lang.Exception

fun ImageView.loadImage(url: String?) {
    try {
        Glide
            .with(context)
            .load(url)
            .error(R.drawable.no_image_available_img)
            .optionalFitCenter()
            .into(this)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}