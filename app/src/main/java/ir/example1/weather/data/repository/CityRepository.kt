package ir.example1.weather.data.repository

import ir.example1.weather.BuildConfig
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.response.CityResponse

class CityRepository(val api: ApiServices) {

    suspend fun getCities(
        query: String,
        limit: Int = 10
    ): Result<CityResponse> {
        return try {
            val response = api.getCitiesList(query, limit, BuildConfig.WEATHER_API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}