package dev.chernyshev.cartracker.domain.entity

data class UsersList(
    val data: List<UserInfo> = emptyList()
)

data class UserInfo(
    val userId: Int? = null,
    val owner: UserPersonalData? = null,
    val vehicles: List<Vehicle> = emptyList()
)

data class UserPersonalData(
    val name: String,
    val surname: String,
    val foto: String
)

data class Vehicle(
    val vehicleid: Int,
    val make: String,
    val model: String,
    val year: String,
    val color: String,
    val vin: String,
    val foto: String
)