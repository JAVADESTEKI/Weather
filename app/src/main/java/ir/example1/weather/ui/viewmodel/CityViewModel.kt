package ir.example1.weather.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.example1.weather.data.repository.CityRepository
import ir.example1.weather.data.remote.response.CityResponse
import kotlinx.coroutines.launch


class CityViewModel(private val repository: CityRepository) : ViewModel() {

    private val _cities = MutableLiveData<CityResponse>()
    val cities: LiveData<CityResponse> = _cities

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCities(query: String, limit: Int = 10) {
        if (query.length < 2) return

        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getCities(query, limit)
                if (result.isSuccess) {
                    _cities.value = result.getOrNull()
                } else {
                    _error.value = "خطا در دریافت لیست شهرها"
                }
            } catch (e: Exception) {
                _error.value = "خطای شبکه: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}