package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(val status : String,val places : List<Place>)

data class Place(val name : String,val location : Location,
           @SerializedName("formatted_address") val address : String)

data class Location(val lng : String, val lat : String)

//PlaceResponse（根数据类）
//封装接口的整体响应，包含两个核心字段：

//status：请求状态标识（如 "ok" 表示成功，"error" 表示失败）。
//places：地点列表，类型为 List<Place>，存储搜索到的所有地点信息。
//Place（单个地点信息）
//描述一个具体地点的详情，包含：
//name：地点名称（如 “北京天安门”）。
//location：地点的经纬度信息，类型为 Location。
//address：格式化地址（如 “北京市东城区东长安街”），通过 @SerializedName("formatted_address") 与 JSON 中的 formatted_address 字段映射（因字段名不一致）。
//Location（经纬度信息）
//存储地点的地理坐标：
//lng：经度（字符串类型，如 “116.407526”）。
//lat：纬度（字符串类型，如 “39.904030”）。