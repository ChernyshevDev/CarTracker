package dev.chernyshev.cartracker.utils.extensions

import android.content.Context
import android.graphics.Point
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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

suspend fun GoogleMap.moveCameraSmoothly(
    position: LatLng?,
    zoom: Float? = null,
    markerInTop: Boolean = false,
    viewHeight: Int? = null
) {
    zoom?.let {
        moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, it))
    }

    val latLng = if (markerInTop && viewHeight != null) {
        val markerPoint = projection.toScreenLocation(position)
        val targetPoint = Point(markerPoint.x, (markerPoint.y + viewHeight / 3.33).toInt())
        projection.fromScreenLocation(targetPoint)
    } else {
        position
    }

    animateCamera(
        CameraUpdateFactory.newLatLng(latLng),
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

fun GoogleMap.placeVehicleMarker(
    position: LatLng,
    title: String,
    vehicleId: Int
): Marker {
    return addMarker(
        MarkerOptions()
            .position(position)
            .title(title)
    ).also {
        it.tag = vehicleId
    }
}