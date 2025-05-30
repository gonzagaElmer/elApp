@file:Suppress("DEPRECATION")

package com.example.elapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.elapp.Fragment.AddNoteFragment
import com.example.elapp.Fragment.MenuFragment
import com.example.elapp.Fragment.ShowNoteFragment
import com.example.elapp.Fragment.WeatherFragment
import com.example.elapp.Model.DailyWeather
import com.example.elapp.R
import com.example.elapp.Helper.AppHelper
import com.example.elapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val URL = "https://api.openweathermap.org/data/2.5/forecast?units=imperial&lat=12.8797&lon=121.7740&appid=58a6416b2fc55b29f277ba7f90f05cf7"
    private val DELAY_MILLS = 2000L
    private var mProgressDialog: ProgressDialog? = null
    private var mDailyWeather:DailyWeather? = null
    private var mDailyWeatherList:ArrayList<DailyWeather> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setContentLocation()
        setListeners(savedInstanceState)

        mProgressDialog = ProgressDialog(this)

        if (AppHelper.isNetworkConnected(this)) {
            loadData()
        } else {
            AppHelper.showToastMsg(this, "No internet connection")
        }
    }

    private fun loadData() {
        mProgressDialog?.setMessage("Fetching data...")
        mProgressDialog?.show()

        Handler().postDelayed({
            fetch()
        }, DELAY_MILLS)

    }

    private fun fetch() {
        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            URL,
            { response ->
                dismissDialogAndIsShowError(false)
                try {
                    // get the response obj
                    val responseObj = JSONObject(response)

                    // get address
                    val cityObj = responseObj.getJSONObject("city")
                    val cityName = cityObj.getString("name")
                    val cityCountry = cityObj.getString("country")

                    // get list
                    val listObj = responseObj.getJSONArray("list")
                    mDailyWeatherList.clear()
                    for (i in 0 until listObj.length()) {
                        // get item
                        val itemObj = listObj.getJSONObject(i)

                        // get item's main
                        val mainObj = itemObj.getJSONObject("main")
                        val temp = mainObj.getDouble("temp")
                        val tempMax = mainObj.getDouble("temp_max")
                        val tempMin = mainObj.getDouble("temp_min")

                        // get item's weather
                        val weatherObj = itemObj.getJSONArray("weather").getJSONObject(0)
                        val weatherMain = weatherObj.getString("main")
                        val weatherDescription = weatherObj.getString("description")

                        // get item's date
                        val date = itemObj.getString("dt_txt")

                        // set data to model and add to list
                        mDailyWeather = DailyWeather(cityName, cityCountry, temp.toInt(), tempMin.toInt(), tempMax.toInt(), weatherMain, weatherDescription, date)
                        mDailyWeatherList.add(mDailyWeather!!)
                    }

                    updateMainUi()

                } catch (e: Exception) {
                    dismissDialogAndIsShowError(true)
                }
            }, {
                dismissDialogAndIsShowError(true)
            }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun dismissDialogAndIsShowError(isShowError:Boolean) {
        mProgressDialog?.dismiss()
        if (isShowError) {
            AppHelper.showToastMsg(this, "Fetching error.")
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun updateMainUi() {
        val headerDataindex = 0;
        if (mDailyWeatherList.size > 0) {
            val todayWeather = mDailyWeatherList[headerDataindex]
            mBinding.countryNameAndCode.text = "${todayWeather.countryName}, ${todayWeather.countryCode}"
            mBinding.currentTemp.text = AppHelper.convertToCelcius(todayWeather.currentTemp).toString() + "°C"
            mBinding.currentMaxTemp.text = AppHelper.convertToCelcius(todayWeather.maxTemp).toString() + "°C"
            mBinding.currentMinTemp.text = AppHelper.convertToCelcius(todayWeather.minTemp).toString() + "°C"
            mBinding.currentWeatherType.text = AppHelper.getWeatherType(todayWeather.weatherType.toString())
            Picasso.get().load("https://picsum.photos/200").into(mBinding.currentWeatherIcon)
            mBinding.currentDate.text = todayWeather.formattedDate
            mBinding.currentDesc.text = "There will be ${todayWeather.weatherDescription} today."
        }
    }

    private fun setListeners(savedInstanceState: Bundle?) {
        mBinding.header.menuBtn.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, MenuFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        mBinding.weatherLayout.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, WeatherFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        mBinding.addNoteBtn.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, AddNoteFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        mBinding.showNotesBtn.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, ShowNoteFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    // start after toolbar ot nudge
    private fun setContentLocation() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}