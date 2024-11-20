package com.jorge.lab3_ex1.ViewModel

import android.util.Log
import com.jorge.lab3_ex1.RoomDB.City
import com.jorge.lab3_ex1.RoomDB.CityDAO
import com.jorge.lab3_ex1.data.RetrofitClass
import com.jorge.lab3_ex1.data.WeatherObject

class AppRepository(private val cityDao: CityDAO) {

    private val apiService = RetrofitClass.api
    private val weatherApiService = RetrofitClass.weatherApi

    suspend fun getCities(query: String): List<String> {
        return try {
            val response = apiService.getCities(query)
            Log.d("AppRepository", "Response: ${response.joinToString()}")
            response
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching cities", e)
            emptyList()
        }
    }

    suspend fun getWeather(city: String): WeatherObject?{
        //return  weatherApiService.getWeather(city,"071c3ffca10be01d334505630d2c1a9c", "metric")
        return  weatherApiService.getWeather(city,"d1c7fe958a8c5cd89b1d18f1dd7ddb33", "metric")
    }

    suspend fun getCitiesFromDB(): List<City>{
        return cityDao.getAllCities()
    }

    suspend fun insertCity(c:City){
        cityDao.insertCityToDB(c)
    }

    suspend fun deleteCity(c:City){
        cityDao.deleteCity(c)
    }

    suspend fun searchForCityInDB(term:String) : List<City>{
        return cityDao.getCityNamed(term)
    }
    //// 44, Toronto, ON, Canda
    // 44, Toronto, Canada
    suspend fun update(newCity: City){
        return cityDao.updateCity(newCity)// 44, Toronto, ON, Canda
    }


}