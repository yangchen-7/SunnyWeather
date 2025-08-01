package com.sunnyweather.android.logic.network

import retrofit2.Call
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    //访问彩云天气API的Retrofit的接口

    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlace(@Query("query") query : String ) : Call<PlaceResponse>

}