package dev.chernyshev.cartracker.domain.contract

import com.google.android.gms.maps.model.LatLng

interface LocationProvider {
    fun getAddress(coordinates: LatLng): String
}