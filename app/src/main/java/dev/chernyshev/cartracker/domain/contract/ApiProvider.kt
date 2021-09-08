package dev.chernyshev.cartracker.domain.contract

import dev.chernyshev.cartracker.domain.entity.UserInfo
import dev.chernyshev.cartracker.domain.entity.VehicleLocationInfo
import dev.chernyshev.cartracker.presentation.main_page.UserId

interface ApiProvider {
    suspend fun getUsers(): List<UserInfo>
    suspend fun getUserVehiclesLocation(userId: UserId): List<VehicleLocationInfo>
}