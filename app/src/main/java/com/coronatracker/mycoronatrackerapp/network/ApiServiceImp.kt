package com.coronatracker.mycoronatrackerapp.network

import com.coronatracker.mycoronatrackerapp.model.Data
import io.reactivex.Observable

object ApiServiceImp {
    private val apiService = RetrofitClient.getClient().create(ApiService::class.java)

    fun getAll(): Observable<Data> {
        return apiService.getAll()
    }

    fun getByCountry(): Observable<List<Data>>{
        return apiService.getByCountries()
    }

    fun getOneCountry(country: String): Observable<Data>{
        return apiService.getOneCountry(country)
    }
}