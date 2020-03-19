package com.coronatracker.coronatracker.network

import com.coronatracker.coronatracker.model.Data
import io.reactivex.Observable
import retrofit2.http.*


interface ApiService {
    @GET("all")
    fun getAll(): Observable<Data>

    @GET("countries")
    fun getByCountries(): Observable<List<Data>>

    @GET("{country}")
    fun getOneCountry(@Path("country") country: String): Observable<Data>
}