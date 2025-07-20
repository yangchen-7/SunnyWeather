package com.sunnyweather.android.logic.dao

import android.content.Context
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object PlaceDao {
    //封装存储和读取数据的接口

    fun savePlace(place: Place){
        val editor = sharedPreferences().edit()
        editor.putString("place", Gson().toJson(place))
        editor.apply()
    }

    fun getSavedPlace() : Place{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    //判断是否有数据被存储
    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather",
        Context.MODE_PRIVATE)


}