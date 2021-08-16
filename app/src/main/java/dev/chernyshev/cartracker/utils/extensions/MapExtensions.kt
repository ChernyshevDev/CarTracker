package dev.chernyshev.cartracker.utils.extensions

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.chernyshev.cartracker.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ZOOM_ANIMATION_TIME = 1000

fun GoogleMap.setCurrentPositionMarker(context: Context, position: LatLng, zoom: Float) {
    addMarker(
        MarkerOptions()
            .position(position)
            .icon(
                BitmapDescriptorFactory.fromBitmap(
                    getBitmapFromVectorDrawable(
                        context,
                        R.drawable.ic_current_position_marker
                    )
                )
            )
    )
    MainScope().launch {
        moveCameraSmoothly(position, zoom)
    }
}

private suspend fun GoogleMap.moveCameraSmoothly(position: LatLng?, zoom: Float) {
    animateCamera(
        CameraUpdateFactory.newLatLngZoom(position, zoom),
        ZOOM_ANIMATION_TIME,
        object : GoogleMap.CancelableCallback {
            override fun onFinish() {
            }

            override fun onCancel() {
            }

        })
    if (this.cameraPosition.zoom != zoom && this.cameraPosition.target != position) {
        delay(ZOOM_ANIMATION_TIME.toLong())
    }
}