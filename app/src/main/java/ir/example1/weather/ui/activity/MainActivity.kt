package ir.example1.weather.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ir.example1.weather.ui.adapter.ForecastAdapter
import ir.example1.weather.R
import ir.example1.weather.ui.viewmodel.WeatherViewModel
import ir.example1.weather.WeatherApplication
import ir.example1.weather.ui.viewmodel.factory.WeatherViewModelFactory
import ir.example1.weather.databinding.ActivityMainBinding
import java.util.Calendar
import kotlin.getValue
import ir.example1.weather.data.remote.response.CurrentWeatherResponse
import ir.example1.weather.data.remote.response.ForecastResponse


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((application as WeatherApplication).weatherRepository)
    }

    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupObservers()
        loadWeatherData()

        binding.addCity.setOnClickListener {
            startActivity(Intent(this, CityListActivity::class.java))
        }

        binding.refreshCurrunt.setOnClickListener {
            loadWeatherData()
        }
    }

    private fun setupWindow() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setupObservers() {
        weatherViewModel.currentWeather.observe(this) { weather ->
            weather?.let {
                updateCurrentWeatherUI(it)
            }
        }

        weatherViewModel.forecastWeather.observe(this) { forecast ->
            forecast?.let {
                updateForecastUI(it)
            }
        }

        weatherViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        weatherViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadWeatherData() {
        val lat = intent.getDoubleExtra("lat", 51.50)
        val lon = intent.getDoubleExtra("lon", -0.12)
        val name = intent.getStringExtra("name") ?: "London"

        binding.txtCity.text = name

        weatherViewModel.loadCurrentWeather(lat, lon)
        weatherViewModel.loadForecastWeather(lat, lon)
    }

    private fun updateCurrentWeatherUI(weather: CurrentWeatherResponse) {
        binding.apply {
            txtStatus.text = weather.weather?.get(0)?.main ?: "-"
            txtWindNum.text = "${weather.wind?.speed?.let { Math.round(it) } ?: 0} Km"
            txtHumidityNum.text = "${weather.main?.humidity ?: 0}%"


            val icon = when (weather?.weather?.get(0)?.icon.toString()) {
                "01d" -> R.drawable.img_clear_sky_day
                "01n" -> R.drawable.img_clear_sky_night
                "02d", "02n", "04d", "04n", "03d", "03n" -> R.drawable.img_cloudy
                "09d", "09n", "10d", "10n" -> R.drawable.img_rainy
                "11d", "11n" -> R.drawable.img_stormy
                "13d", "13n" -> R.drawable.img_snowy
                "50d", "50n" -> R.drawable.img_misty
                else -> R.drawable.img_clear_sky_day
            }
            Glide.with(binding.root.context)
                .load(icon)
                .into(binding.imgTodayCondition)

            txtTodayDegree.text = "${weather.main?.temp?.let { Math.round(it) } ?: 0}°"
            txtMaxDegree.text = "${weather.main?.tempMax?.let { Math.round(it) } ?: 0}°"
            txtMinDegree.text = "${weather.main?.tempMin?.let { Math.round(it) } ?: 0}°"

            updateCurrentDescriptionCardInfo(weather)
        }
    }

    private fun updateCurrentDescriptionCardInfo(weather: CurrentWeatherResponse) {
        val icon = when (weather.weather?.get(0)?.main ?: "-") {
            "Rain", "Drizzle", "Thunderstorm" -> {
                binding.txtDescriptionStatus.text =
                    weather.rain?.h.toString() + " mm/h"
                binding.txtDescription.text = "Last 1h rain"
                R.drawable.img_rainy
            }

            "Clouds" -> {
                binding.txtDescriptionStatus.text =
                    weather.clouds?.all.toString() + " %"
                binding.txtDescription.text = "Cloudiness"
                R.drawable.img_cloudy

            }

            "Snow" -> {
                binding.txtDescriptionStatus.text =
                    weather.weather?.get(0)?.description.toString() + " "
                binding.txtDescription.text = "Status"
                R.drawable.img_snowy
            }

            "Mist", "Fog", "Haze" -> {
                binding.txtDescriptionStatus.text =
                    weather?.visibility.toString() + " m"
                binding.txtDescription.text = "Visibility"
                R.drawable.img_visibility
            }

            else -> {
                binding.txtDescriptionStatus.text =
                    weather?.main?.pressure.toString() + " hPAa"
                binding.txtDescription.text = "Pressure"
                R.drawable.img_pressure
            }
        }
        Glide.with(binding.root.context)
            .load(icon)
            .into(binding.imgDescription)


    }

    private fun updateForecastUI(forecast: ForecastResponse) {
        forecast.list?.let { forecastList ->
            forecastAdapter.differ.submitList(forecastList)
            binding.forcastRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = forecastAdapter
            }
        }
    }
}
