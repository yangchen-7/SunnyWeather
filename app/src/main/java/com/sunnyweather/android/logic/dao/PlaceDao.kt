package com.sunnyweather.android.logic.dao

import android.content.Context
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object PlaceDao {
    //封装存储和读取数据的接口

    //使用sharedPreferences存储记录
    fun savePlace(place: Place){
        val editor = sharedPreferences().edit()
        //将数据转换成json字符串
        editor.putString("place", Gson().toJson(place))
        editor.apply()
    }

    fun getSavedPlace() : Place{
        val placeJson = sharedPreferences().getString("place","")
        //将json字符串解析成Place对象
        return Gson().fromJson(placeJson, Place::class.java)
    }

    //判断是否有数据被存储
    fun isPlaceSaved() = sharedPreferences().contains("place")

    //获取sharedPreferences实例
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather",
        Context.MODE_PRIVATE)


}