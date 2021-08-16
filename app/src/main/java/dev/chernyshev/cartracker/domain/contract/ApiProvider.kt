package dev.chernyshev.cartracker.domain.contract

import dev.chernyshev.cartracker.domain.entity.UserInfo

interface ApiProvider {
    fun getUsers() : List<UserInfo>
}