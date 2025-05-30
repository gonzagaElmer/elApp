package com.example.elapp.Model

import com.example.elapp.Helper.AppHelper

class DailyWeather {
    var countryName:String? = null
    var countryCode:String? = null
    var currentTemp:Int? = null
    var minTemp:Int? = null
    var maxTemp:Int? = null
    var weatherType:String? = null
    var weatherDescription:String? = null
    var formattedDate:String? = null

    constructor(countryName: String?, countryCode: String?, currentTemp: Int?, minTemp: Int?, maxTemp: Int?, weatherType: String?, weatherDescription: String?, rawDate: String) {
        this.countryName = countryName
        this.countryCode = countryCode
        this.currentTemp = currentTemp
        this.minTemp = minTemp
        this.maxTemp = maxTemp
        this.weatherType = weatherType
        this.weatherDescription = weatherDescription
        this.formattedDate = AppHelper.formatRawDate(rawDate)
    }
}