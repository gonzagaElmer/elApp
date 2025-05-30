package com.example.elapp.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity.CONNECTIVITY_SERVICE
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.Toast

class AppHelper {
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

        @Suppress("DEPRECATION")
        @SuppressLint("ServiceCast")
        fun isNetworkConnected(context:Context): Boolean {
            val connectivityManager = context.getSystemService( CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


        fun showToastMsg(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}