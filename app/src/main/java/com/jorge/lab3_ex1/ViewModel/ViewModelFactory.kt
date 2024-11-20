package com.jorge.lab3_ex1.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class ViewModelFactory(private val repository: AppRepository):
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitiesViewModel::class.java)){
            return CitiesViewModel(repository) as T
        }else {
            throw IllegalArgumentException("Error")
        }
    }
}


class WeatherViewModelFactory(private val repository: AppRepository):
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            return WeatherViewModel(repository) as T
        }else {
            throw IllegalArgumentException("Error")
        }
    }
}