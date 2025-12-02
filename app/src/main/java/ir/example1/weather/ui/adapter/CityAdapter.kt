package ir.example1.weather.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.example1.weather.ui.activity.MainActivity
import ir.example1.weather.databinding.CityViewholderBinding
import ir.example1.weather.data.remote.response.CityResponse

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CityViewholderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CityViewholderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = differ.currentList[position]

        holder.binding.txtcityFinds.text = city.name ?: "نامشخص"
        holder.binding.txtContryOfcityFinds.text = city.country ?: "نامشخص"

        holder.binding.root.setOnClickListener {
            val intent = Intent(holder.binding.root.context, MainActivity::class.java).apply {
                putExtra("lat", city.lat)
                putExtra("lon", city.lon)
                putExtra("name", city.name)
            }
            holder.binding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<CityResponse.CityItem>() {

        override fun areItemsTheSame(
            oldItem: CityResponse.CityItem,
            newItem: CityResponse.CityItem
        ): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.lat == newItem.lat &&
                    oldItem.lon == newItem.lon
        }

        override fun areContentsTheSame(
            oldItem: CityResponse.CityItem,
            newItem: CityResponse.CityItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}