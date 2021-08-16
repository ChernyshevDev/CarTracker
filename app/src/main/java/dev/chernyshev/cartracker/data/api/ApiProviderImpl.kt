package dev.chernyshev.cartracker.data.api

import com.google.gson.GsonBuilder
import dev.chernyshev.cartracker.domain.contract.ApiProvider
import dev.chernyshev.cartracker.domain.entity.UserInfo
import dev.chernyshev.cartracker.domain.entity.UsersList
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
            delay(repeatRequestDelay)
            getUsers()
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
}