package ir.example1.weather.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.example1.weather.data.repository.CityRepository
import ir.example1.weather.ui.viewmodel.CityViewModel

class CityViewModelFactory(private val repository: CityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}