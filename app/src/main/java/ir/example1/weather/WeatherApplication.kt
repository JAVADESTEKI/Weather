package ir.example1.weather

import android.app.Application
import ir.example1.weather.data.repository.CityRepository
import ir.example1.weather.data.repository.WeatherRepository
import ir.example1.weather.data.remote.api.ApiClient

class WeatherApplication : Application() {
    val weatherRepository: WeatherRepository by lazy {
        val apiService = ApiClient.apiServices
        WeatherRepository(apiService)
    }

    val cityRepository: CityRepository by lazy {
        val apiService = ApiClient.apiServices
        CityRepository(apiService)
    }
}