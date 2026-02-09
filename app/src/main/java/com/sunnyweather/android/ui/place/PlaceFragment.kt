package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity


class PlaceFragment : Fragment() {
//获取viewModel实例
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //当之前存储了地址数据的时候，现在打开可以直接显示
        if (activity is MainActivity && viewModel.isPlaceSaved()){
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val  recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        val searchPlaceEdit = view?.findViewById<EditText>(R.id.searchPlaceEdit)
        val bgImageView = view?.findViewById<ImageView>(R.id.bgImageView)

        //给recycleview设置layoutManager和适配器
        val layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        recyclerView?.adapter = adapter

        //监听搜索框内容的变化
        searchPlaceEdit?.doAfterTextChanged { editable ->
            val content = editable?.toString().orEmpty()
            if (content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                recyclerView?.visibility = View.GONE
                bgImageView?.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        //使用 viewLifecycleOwner 确保观察范围与 Fragment 的视图生命周期一致，避免内存泄漏
        viewModel.placeLiveData.observe(viewLifecycleOwner){ result ->
            //result.getOrNull() 获取可能为空的地点列表（place）
            val place = result.getOrNull()
            if (place != null){
                //显示
                recyclerView?.visibility = View.VISIBLE
                //隐藏
                bgImageView?.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(place)
                //用于通知适配器其内部数据源已经发生变化，需要重新计算并刷新整个列表的显示
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity,"未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }

    }

}