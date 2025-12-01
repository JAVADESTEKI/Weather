package ir.example1.weather.data.repository

import ir.example1.weather.BuildConfig
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.response.CurrentWeatherResponse
import ir.example1.weather.data.remote.response.ForecastResponse

class WeatherRepository(val api: ApiServices) {

    suspend fun getCurrentWeather(
        lat: Double,
        lng: Double,
        unit: String
    ): Result<CurrentWeatherResponse> {
        return try {
            val response = api.getCurrentWeather(lat, lng, unit, BuildConfig.WEATHER_API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getForecastWeather(
        lat: Double,
        lng: Double,
        unit: String
    ): Result<ForecastResponse> {
        return try {
            val response = api.getForecastWeather(lat, lng, unit, BuildConfig.WEATHER_API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}