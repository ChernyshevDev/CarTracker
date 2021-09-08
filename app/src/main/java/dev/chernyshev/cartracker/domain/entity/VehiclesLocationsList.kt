package dev.chernyshev.cartracker.domain.entity

data class VehiclesLocationsList(
    val data: List<VehicleLocationInfo>
)

data class VehicleLocationInfo(
    val vehicleid: Int,
    val lat: Double,
    val lon: Double,
    var ownerId: Int? = null
)