package dev.chernyshev.cartracker.domain.contract

import dev.chernyshev.cartracker.domain.entity.UserInfo

interface ApiProvider {
    suspend fun getUsers() : List<UserInfo>
}