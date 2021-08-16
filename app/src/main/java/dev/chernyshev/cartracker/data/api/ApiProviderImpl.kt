package dev.chernyshev.cartracker.data.api

import com.google.gson.GsonBuilder
import dev.chernyshev.cartracker.domain.contract.ApiProvider
import dev.chernyshev.cartracker.domain.entity.UserInfo
import dev.chernyshev.cartracker.domain.entity.UsersList
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

private const val scopeTechnologyApiUrl = "http://mobi.connectedcar360.net"

class ApiProviderImpl @Inject constructor() : ApiProvider {
    override fun getUsers(): List<UserInfo> {
        return getRetrofit()
            .getUsersList()
            .execute()
            .body()
            ?.data ?: emptyList()
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