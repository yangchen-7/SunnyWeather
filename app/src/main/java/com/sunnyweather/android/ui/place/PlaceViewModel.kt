package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {

    //MutableLiveData是一种可变的liveData。它允许你通过setValue()或postValue()方法更新数据，并在数据变化时通知所有观察者。
    //getValue（） 获取liveData中包含的数据
    //setValue（） 给liveData设置数据，只能在主线程中调用。
    //postValue（） 用于给非主线程liveData设置数据

    private val searchLiveData = MutableLiveData<String>()

    //对界面上显示的城市数据进行缓存
    val placeList = ArrayList<Place>()

    //用switchMap方法观察对象，否则返回的livedata对象无法进行观察
    val placeLiveData = searchLiveData.switchMap { query ->
        //调用仓库层的方法进行网络请求，并将返回的livedata对象转换成可供activity观察的livedata对象
        Repository.searchPlaces(query)
    }
    ////将传入的搜索参数赋值给searchLiveData对象，
    fun searchPlaces(query : String){
        searchLiveData.value = query
    }

    //封装仓库层中的存储
    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()

}