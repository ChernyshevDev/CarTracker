package dev.chernyshev.cartracker.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun getBitmapFromVectorDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap? {
    var returnBitmap: Bitmap? = null
    val drawable = ContextCompat.getDrawable(context, drawableId)
    drawable?.let {
        val bitmap = Bitmap.createBitmap(
            it.intrinsicWidth,
            it.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        it.setBounds(0, 0, canvas.width, canvas.height)
        it.draw(canvas)
        returnBitmap = bitmap
    }
    return returnBitmap
}