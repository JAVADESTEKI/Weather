package ir.example1.weather.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.example1.weather.data.repository.WeatherRepository
import ir.example1.weather.data.remote.response.CurrentWeatherResponse
import ir.example1.weather.data.remote.response.ForecastResponse
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _currentWeather = MutableLiveData<CurrentWeatherResponse>()
    val currentWeather: LiveData<CurrentWeatherResponse> = _currentWeather

    private val _forecastWeather = MutableLiveData<ForecastResponse>()
    val forecastWeather: LiveData<ForecastResponse> = _forecastWeather

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCurrentWeather(lat: Double, lng: Double, unit: String = "metric") {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getCurrentWeather(lat, lng, unit)
                if (result.isSuccess) {
                    _currentWeather.value = result.getOrNull()
                } else {
                    _error.value = "خطا در دریافت آب و هوا"
                }
            } catch (e: Exception) {
                _error.value = "خطای شبکه: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadForecastWeather(lat: Double, lng: Double, unit: String = "metric") {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getForecastWeather(lat, lng, unit)
                if (result.isSuccess) {
                    _forecastWeather.value = result.getOrNull()
                } else {
                    _error.value = "خطا در دریافت پیش‌بینی"
                }
            } catch (e: Exception) {
                _error.value = "خطای شبکه: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}