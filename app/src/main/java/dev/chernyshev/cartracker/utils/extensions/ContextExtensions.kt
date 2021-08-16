package dev.chernyshev.cartracker.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.lang.NullPointerException

fun Context.checkIfPermissionGranted(
    permission: String,
    onAlreadyGranted: (() -> Unit)? = null,
    onNoPermission: (() -> Unit)? = null
) {
    if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
        onAlreadyGranted?.invoke()
    } else {
        onNoPermission?.invoke()
    }
}

@SuppressLint("MissingPermission")
fun Context.getLastKnownLocation(onLocationReady: (LatLng) -> Unit, onError: () -> Unit) {
    LocationServices.getFusedLocationProviderClient(this)
        .lastLocation
        .addOnSuccessListener {
            try {
                onLocationReady(LatLng(it.latitude, it.longitude))
            } catch (npe: NullPointerException) {
                onError()
            }
        }
}