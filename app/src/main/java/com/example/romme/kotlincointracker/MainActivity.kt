package com.example.romme.kotlincointracker

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.romme.kotlincointracker.Adapter.CoinAdapter
import com.example.romme.kotlincointracker.Common.Common
import com.example.romme.kotlincointracker.Interface.ILoadMore
import com.example.romme.kotlincointracker.Model.CoinModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.security.auth.callback.Callback
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity(),ILoadMore{
    //Declare variable
    internal var items:MutableList<CoinModel> = ArrayList()
    internal  lateinit var adapter: CoinAdapter
    internal  lateinit var client:OkHttpClient
    internal lateinit var  request:Request


    override fun OnLoadMore() {
        if (items.size <= Common.MAX_COIN_LOAD)
            loadNext10Coin(items.size)
        else
            Toast.makeText(this@MainActivity,"Data max is "+Common.MAX_COIN_LOAD,Toast.LENGTH_SHORT)
                    .show()

    }

    private fun loadNext10Coin(index: Int) {
        client = OkHttpClient()
        request = Request.Builder()
                .url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",index))
                .build()

        swipe_to_refresh.isRefreshing=true //Show refresh
        client.newCall(request)
                .enqueue(object  :Callback, okhttp3.Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Log.d("Error",e.toString())
                    }

                    override fun onResponse(call: Call?, response: Response) {
                        val body = response.body()!!.string()
                        val gson=Gson()
                        val newItems = gson.fromJson<List<CoinModel>>(body,object :TypeToken<List<CoinModel>>(){}.type)
                        runOnUiThread{
                            items.addAll(newItems)
                            adapter.setLoaded()
                            adapter.updateData(items)

                            swipe_to_refresh.isRefreshing=false
                        }
                    }

                })
    }

    private fun loadFrist10Coin() {
        client = OkHttpClient()
        request = Request.Builder()
                .url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=0&limit=10"))
                .build()


        client.newCall(request)
                .enqueue(object  :Callback, okhttp3.Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Log.d("Error",e.toString())
                    }

                    override fun onResponse(call: Call?, response: Response) {
                        val body = response.body()!!.string()
                        val gson=Gson()
                        items = gson.fromJson(body,object :TypeToken<List<CoinModel>>(){}.type)
                        runOnUiThread{


                            adapter.updateData(items)


                        }
                    }

                })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipe_to_refresh.post {
            loadFrist10Coin()
        }

        swipe_to_refresh.setOnRefreshListener {
            items.clear() //Remove all items
            loadFrist10Coin()
            setUpAdapter()
        }

        // in tutorial 37:00
        coin_recycler_view.layoutManager = LinearLayoutManager(this)
        setUpAdapter()
    }

    private fun setUpAdapter () {
        adapter = CoinAdapter(coin_recycler_view as RecyclerView, this@MainActivity,items)
        (coin_recycler_view as RecyclerView).adapter = adapter
        adapter.setLoadMore(this)
    }
}


