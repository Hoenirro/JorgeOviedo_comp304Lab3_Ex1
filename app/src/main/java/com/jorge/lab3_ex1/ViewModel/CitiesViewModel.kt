package com.jorge.lab3_ex1.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jorge.lab3_ex1.RoomDB.City
import com.jorge.lab3_ex1.data.WeatherObject
import kotlinx.coroutines.launch

class CitiesViewModel(private val repository: AppRepository) : ViewModel() {

    var cities by mutableStateOf<List<String>>(emptyList())
        private set

    var dbcities by mutableStateOf<List<City>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            cities = repository.getCities("")
            dbcities = repository.getCitiesFromDB()
        }
    }

    fun getCities(userTerm: String) {
        viewModelScope.launch {
            try {
                val fetchCities = repository.getCities(userTerm)
                Log.d("getCities", "Fetched Cities: ${fetchCities.joinToString()}")
                cities = fetchCities
            } catch (e: Exception) {
                Log.e("getCities", "Error fetching cities", e)
            }
        }
    }


    fun getDBCities() {
        viewModelScope.launch {
            dbcities = repository.getCitiesFromDB()
        }
    }

    fun insertToDB(c: City) {
        viewModelScope.launch {
            repository.insertCity(c)
            dbcities = repository.getCitiesFromDB()
        }
    }

    fun update(newCity: City) {
        viewModelScope.launch {
            repository.update(newCity)
            dbcities = repository.getCitiesFromDB()
        }
    }

    fun deleteOneCity(c: City) {
        viewModelScope.launch {
            repository.deleteCity(c)
            dbcities = repository.getCitiesFromDB()
        }
    }
}
