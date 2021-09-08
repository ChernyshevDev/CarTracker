package dev.chernyshev.cartracker.data.api

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import dev.chernyshev.cartracker.domain.contract.LocationProvider
import java.util.Locale
import javax.inject.Inject

class LocationProviderImpl @Inject constructor(
    private val context: Context,
) : LocationProvider {
    override fun getAddress(coordinates: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addressList = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
        var address: String? = null
        addressList.getOrNull(0)?.let {
            address = it.getAddressLine(0)
        }
        return address ?: ""
    }
}