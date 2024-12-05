package com.example.weatherapp.Utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class Helper {
    companion object {
        fun convertToCelcius(Fahrenheit: Int?): Int {
            if (Fahrenheit == null) {
                return 0
            }
            return (Fahrenheit - 32) * 5 / 9
        }

        fun getWeatherType(type: String): String {
            return when (type) {
                "Clouds" -> "Cloudy"
                "Clear" -> "Clear"
                "Rain" -> "Rainy"
                "Wind" -> "Windy"
                "Snow" -> "Snowy"
                else -> "Unknown"
            }
        }

        fun formatRawDate(rawDate: String): String? {
            if (rawDate.isNullOrEmpty()) {
                return null
            }

            val inputPattern = "yyyy-MM-dd HH:mm:ss"
            val outputPattern = "MMM d, yyyy hh:mma"
            val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())

            return try {
                val date = inputFormat.parse(rawDate)
                date?.let { outputFormat.format(it) }
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }
    }
}