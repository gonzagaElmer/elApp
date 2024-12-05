@file:Suppress("DEPRECATION")

package com.example.weatherapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Adapter.MainAdapter
import com.example.weatherapp.Fragment.MenuFragment
import com.example.weatherapp.Model.DailyWeather
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Helper
import com.example.weatherapp.databinding.ActivityMainBinding
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
        setView()

        mProgressDialog = ProgressDialog(this)

        if (isNetworkConnected()) {
            loadData()
        } else {
            showToastMsg("No internet connection")
        }
    }

    private fun setView() {
        mBinding.weatherRv.layoutManager = LinearLayoutManager(this)
        mBinding.weatherRv.adapter = MainAdapter(mDailyWeatherList)
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
            showToastMsg("Fetching error.")
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun updateMainUi() {
        val headerDataindex = 0;
        if (mDailyWeatherList.size > 0) {
            val todayWeather = mDailyWeatherList[headerDataindex]
            mBinding.countryNameAndCode.text = "${todayWeather.countryName}, ${todayWeather.countryCode}"
            mBinding.currentTemp.text = Helper.convertToCelcius(todayWeather.currentTemp).toString() + "°C"
            mBinding.currentMaxTemp.text = Helper.convertToCelcius(todayWeather.maxTemp).toString() + "°C"
            mBinding.currentMinTemp.text = Helper.convertToCelcius(todayWeather.minTemp).toString() + "°C"
            mBinding.currentWeatherType.text = Helper.getWeatherType(todayWeather.weatherType.toString())
            Picasso.get().load("https://picsum.photos/200").into(mBinding.currentWeatherIcon)
            mBinding.currentDate.text = todayWeather.formattedDate
            mBinding.currentDesc.text = "There will be ${todayWeather.weatherDescription} today."
        }
        // remove the displayed data
        mDailyWeatherList.removeAt(headerDataindex)
        mBinding.weatherRv.adapter?.notifyDataSetChanged()
    }

    private fun setListeners(savedInstanceState: Bundle?) {
        mBinding.header.menuBtn.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, MenuFragment.newInstance("param1", "param2"))
                    .commit()
            }
        }

        mBinding.swipeRefreshList.setOnRefreshListener {
            if (mBinding.swipeRefreshList.isRefreshing) {
                mBinding.swipeRefreshList.isRefreshing = false
            }

            if (isNetworkConnected()) {
                loadData()
            } else {
                showToastMsg("No internet connection")
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

    // create a function to check if user is connected to the internet or wifi
    @Suppress("DEPRECATION")
    @SuppressLint("ServiceCast")
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    private fun showToastMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}