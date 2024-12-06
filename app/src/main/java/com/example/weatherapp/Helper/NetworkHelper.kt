package com.example.weatherapp.Helper

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NetworkHelper {


    interface NetworkCallback{
        fun onSuccess(responseObj: JSONObject)
        fun onError(errorStr: String)
    }

    companion object {
        private const val URL = "https://api.openweathermap.org/data/2.5/forecast?units=imperial&lat=12.8797&lon=121.7740&appid=58a6416b2fc55b29f277ba7f90f05cf7"

        val networkHelper = NetworkHelper()

        fun fetchData(context: Context, callback: NetworkCallback){
            val queue = Volley.newRequestQueue(context)

            // Request a string response from the provided URL.
            val stringRequest = JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                { response ->
                    callback.onSuccess(response)
                }, {
                    callback.onError(it.message.toString())
                }
            )

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }
    
}