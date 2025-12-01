package ir.example1.weather.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.example1.weather.R
import ir.example1.weather.databinding.ForecastViewholderBinding
import ir.example1.weather.data.remote.response.ForecastResponse
import java.text.SimpleDateFormat
import java.util.Calendar

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ForecastViewholderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ForecastViewholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecastItem = differ.currentList[position]

        // تاریخ و ساعت
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(forecastItem.dtTxt.toString())
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfWeekName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> "-"
        }
        holder.binding.nameDayTxt.text = dayOfWeekName

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val amPm = if (hour < 12) "am" else "pm"
        val hour12 = calendar.get(Calendar.HOUR)
        holder.binding.hourTxt.text = "$hour12$amPm"

        holder.binding.tempTxt.text = "${forecastItem.main?.temp?.let { Math.round(it) } ?: 0}°"

        // آیکون آب و هوا
        val icon = when (forecastItem.weather?.get(0)?.icon.toString()) {
            "01d" -> R.drawable.img_clear_sky_day
            "01n" -> R.drawable.img_clear_sky_night
            "02d", "02n" , "04d", "04n" ,   "03d", "03n" -> R.drawable.img_cloudy
            "09d", "09n" , "10d", "10n" -> R.drawable.img_rainy
            "11d", "11n" -> R.drawable.img_stormy
            "13d", "13n" -> R.drawable.img_snowy
            "50d", "50n" -> R.drawable.img_misty
            else -> R.drawable.img_clear_sky_day
        }

        Glide.with(holder.binding.root.context)
            .load(icon)
            .into(holder.binding.pic)
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<ForecastResponse.ForecastItem>() {
        override fun areItemsTheSame(
            oldItem: ForecastResponse.ForecastItem,
            newItem: ForecastResponse.ForecastItem
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponse.ForecastItem,
            newItem: ForecastResponse.ForecastItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}