package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    //网络数据源访问入口，对所有网络请求的API进行封装

    //创建动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    //当外部调用这个方法的时候，当前协程会暂停。直到服务器响应请求后，并返回数据才恢复
    suspend fun searchPlaces(query : String) = placeService.searchPlace(query).await()

    //await()函数 解析服务器返回的数据并返回  服务器响应后会回调到enqueue（）方法中
    private suspend fun <T> Call<T>.await() : T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T?>, response: Response<T?>) {
                    //请求成功
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
                //请求失败
                override fun onFailure(call: Call<T?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng : String, lat : String) = weatherService.getDailyWeather(lng,lat).await()

    suspend fun getRealtimeWeather(lng : String, lat : String) = weatherService.getRealtimeWeather(lng,lat).await()




}