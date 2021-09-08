package dev.chernyshev.cartracker.data.api

import com.google.gson.GsonBuilder
import dev.chernyshev.cartracker.domain.contract.ApiProvider
import dev.chernyshev.cartracker.domain.entity.UserInfo
import dev.chernyshev.cartracker.domain.entity.UsersList
import dev.chernyshev.cartracker.domain.entity.VehicleLocationInfo
import dev.chernyshev.cartracker.domain.entity.VehiclesLocationsList
import dev.chernyshev.cartracker.presentation.main_page.UserId
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

private const val scopeTechnologyApiUrl = "http://mobi.connectedcar360.net"
private const val repeatRequestDelay = 1_500L

class ApiProviderImpl @Inject constructor() : ApiProvider {
    override suspend fun getUsers(): List<UserInfo> {
        return try {
            val data = getRetrofit()
                .getUsersList()
                .execute()
                .body()
                ?.data ?: emptyList()

            if (data.isEmpty()) {
                throw Exception("no user data available")
            }
            data
        } catch (e: Exception) {
            // That`s not common practice though. However this particular api provider throws error quite often
            delay(repeatRequestDelay)
            getUsers()
        }
    }

    override suspend fun getUserVehiclesLocation(userId: UserId): List<VehicleLocationInfo> {
        return try {
            val data = getRetrofit()
                .getUserVehiclesLocations(userId = userId.toString())
                .execute()
                .body()
                ?.data ?: emptyList()

            if (data.isEmpty()) {
                throw Exception("no user vehicles data available")
            }
            data
        } catch (e: Exception) {
            // That`s not common practice though. However this particular api provider throws error quite often
            delay(repeatRequestDelay)
            getUserVehiclesLocation(userId)
        }
    }

    private fun getRetrofit(): ScopeTechnologyApi {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(scopeTechnologyApiUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ScopeTechnologyApi::class.java)
    }
}

interface ScopeTechnologyApi {

    @GET("/api/?")
    fun getUsersList(
        @Query("op") op: String = "list"
    ): Call<UsersList>

    @GET("/api/?")
    fun getUserVehiclesLocations(
        @Query("op") op: String = "getlocations",
        @Query("userid") userId: String
    ): Call<VehiclesLocationsList>
}