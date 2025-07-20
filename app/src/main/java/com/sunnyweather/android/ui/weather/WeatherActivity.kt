package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }

        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }

        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        //绑定下拉刷新的控件
        val swipeRefresh : SwipeRefreshLayout = findViewById(R.id.swipeRefresh)

        viewModel.weatherLiveData.observe(this, Observer{result ->
            val weather = result.getOrNull()
            if (weather != null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }

            swipeRefresh.isRefreshing = false
        })
        //设置下拉刷新进度跳的颜色
        swipeRefresh.setColorSchemeResources(R.color.purple_500)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        //滑动菜单
        val navBtn : Button = findViewById(R.id.navBtn)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        navBtn.setOnClickListener {
            //让 DrawerLayout 控件按照左侧（GravityCompat.START 所指方向 ）的方向展开侧滑菜单
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                //隐藏键盘
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

    }

    fun refreshWeather(){
        val swipeRefresh : SwipeRefreshLayout = findViewById(R.id.swipeRefresh)
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        val placeName : TextView = findViewById(R.id.placeName)
        val currentTemp : TextView = findViewById(R.id.currentTemp)
        val currentSky : TextView = findViewById(R.id.currentSky)
        val currentAQI : TextView = findViewById(R.id.currentAQI)
        val nowLayout : RelativeLayout = findViewById(R.id.nowLayout)
        val forecastLayout : LinearLayout = findViewById(R.id.forecastLayout)
        val coldRiskText : TextView = findViewById(R.id.coldRiskText)
        val dressingText : TextView = findViewById(R.id.dressingText)
        val ultravioletText : TextView = findViewById(R.id.ultravioletText)
        val carWashingText : TextView = findViewById(R.id.carWashingText)
        val weatherLayout : ScrollView = findViewById(R.id.weatherLayout)

        //显示地点
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中数据
        //温度
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        //天气状况
        currentSky.text = getSky(realtime.skycon).info
        //空气指数
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        //设置背景图
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        //清空旧数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        //遍历未来每天的预报数据
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            //加载预报布局
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            //填充日期
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            //填充天气图标和描述
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            //填充温度范围
            temperatureInfo.text = tempText
            //添加到布局
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        // 感冒风险、穿衣建议、紫外线指数、洗车指数
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        //变成可见状态
        weatherLayout.visibility = View.VISIBLE
    }

}


