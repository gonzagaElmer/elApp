package com.example.weatherapp.Fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapter.MainAdapter
import com.example.weatherapp.Model.DailyWeather
import com.example.weatherapp.Helper.NetworkHelper
import com.example.weatherapp.Helper.AppHelper
import com.example.weatherapp.databinding.FragmentWeatherBinding
import org.json.JSONObject

class WeatherFragment : Fragment() {

    private lateinit var mBinding: FragmentWeatherBinding
    private var mDailyWeather: DailyWeather? = null
    private var mDailyWeatherList:ArrayList<DailyWeather> = ArrayList()
    private val DELAY_MILLS = 2000L
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWeatherBinding.inflate(inflater, container, false)

        setListeners()
        setView()
        mProgressDialog = ProgressDialog(requireContext())

        if (AppHelper.isNetworkConnected(requireContext())) {
            loadData()
        } else {
            AppHelper.showToastMsg(requireContext(), "No internet connection")
        }

        return mBinding.root
    }


    private fun loadData() {
        mProgressDialog?.setMessage("Fetching data...")
        mProgressDialog?.show()

        Handler().postDelayed({
            fetch()
        }, DELAY_MILLS)

    }

    private fun setListeners() {
        mBinding.swipeRefreshList.setOnRefreshListener {
            if (mBinding.swipeRefreshList.isRefreshing) {
                mBinding.swipeRefreshList.isRefreshing = false
            }

            if (AppHelper.isNetworkConnected(requireContext())) {
                loadData()
            } else {
                AppHelper.showToastMsg(requireContext(),"No internet connection")
            }
        }

        mBinding.closeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun fetch() {
        NetworkHelper.fetchData(requireContext(), object : NetworkHelper.NetworkCallback {
            override fun onSuccess(responseObj: JSONObject) {
                try {
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

                    dismissDialogAndIsShowError(false)
                    updateMainUi()

                } catch (e: Exception) {
                    dismissDialogAndIsShowError(true)
                }
            }

            override fun onError(errorStr: String) {
                AppHelper.showToastMsg(requireContext(), "Error: $errorStr")
                dismissDialogAndIsShowError(true)
            }
        })
    }

    private fun setView() {
        mBinding.weatherRv.layoutManager = LinearLayoutManager(requireActivity())
        mBinding.weatherRv.adapter = MainAdapter(mDailyWeatherList)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun updateMainUi() {
        val headerDataindex = 0;
        if (mDailyWeatherList.size > 0) {
            // remove the displayed data
            mDailyWeatherList.removeAt(headerDataindex)
        }
        mBinding.weatherRv.adapter?.notifyDataSetChanged()
    }


    private fun dismissDialogAndIsShowError(isShowError:Boolean) {
        mProgressDialog?.dismiss()

        if (isShowError) {
            AppHelper.showToastMsg(requireContext(), "Fetching error.")
        }
    }

}