package ir.example1.weather.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ir.example1.weather.ui.adapter.CityAdapter
import ir.example1.weather.ui.viewmodel.CityViewModel
import ir.example1.weather.ui.viewmodel.factory.CityViewModelFactory
import ir.example1.weather.WeatherApplication
import ir.example1.weather.databinding.ActivityCityListBinding

class CityListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy { CityAdapter() }

    private val cityViewModel: CityViewModel by viewModels {
        CityViewModelFactory((application as WeatherApplication).cityRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupObservers()
        setupSearch()
    }

    private fun setupWindow() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setupObservers() {
        cityViewModel.cities.observe(this) { cities ->
            cities?.let {
                binding.progressBar2.visibility = View.GONE
                cityAdapter.differ.submitList(it)
                binding.cityView.apply {
                    layoutManager = LinearLayoutManager(
                        this@CityListActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = cityAdapter
                }
            }
        }

        cityViewModel.error.observe(this) { error ->
            error?.let {
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        cityViewModel.loading.observe(this) { isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupSearch() {
        binding.cityEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                if (query.length >= 2) {
                    cityViewModel.loadCities(query, 10)
                } else {
                    cityAdapter.differ.submitList(emptyList())
                }
            }
        })
    }
}