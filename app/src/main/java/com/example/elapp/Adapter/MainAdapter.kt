package com.example.elapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elapp.Model.DailyWeather
import com.example.elapp.R
import com.example.elapp.Helper.AppHelper
import com.squareup.picasso.Picasso

class MainAdapter(private var items:List<DailyWeather>):RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val wIcon: ImageView = view.findViewById(R.id.weather_icon)
        private val wDate: TextView = view.findViewById(R.id.weather_date)
        private val wType: TextView = view.findViewById(R.id.weather_type)
        private val wMaxTemp: TextView = view.findViewById(R.id.weather_max_temp)
        private val wMinTemp: TextView = view.findViewById(R.id.weather_min_temp)

        @SuppressLint("SetTextI18n")
        fun bind(item: DailyWeather, position: Int) {
            Picasso.get().load("https://picsum.photos/200").into(wIcon)
            wDate.text = item.formattedDate
            wType.text = "${item.weatherType} is expected on this day."
            wMaxTemp.text = AppHelper.convertToCelcius(item.maxTemp).toString() + "°C"
            wMinTemp.text = AppHelper.convertToCelcius(item.minTemp).toString() + "°C"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_weather, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}