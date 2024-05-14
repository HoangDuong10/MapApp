package com.example.map.api

import com.example.map.model.ResponseItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("autocomplete")
    suspend fun getListAddress(@Query("apiKey") apiKey: String,
                               @Query("q") q: String, @Query("limit") limit: Int,
                               @Query("in") countryCode: String,
                               @Query("lang") lang : String) : Response<ResponseItem>
}