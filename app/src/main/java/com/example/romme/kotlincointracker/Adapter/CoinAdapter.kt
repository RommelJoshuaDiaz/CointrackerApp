package com.example.romme.kotlincointracker.Adapter

import android.app.Activity
import android.graphics.Color
import android.support.v4.R
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.romme.kotlincointracker.Common.Common
import com.example.romme.kotlincointracker.Interface.ILoadMore
import com.example.romme.kotlincointracker.Model.CoinModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.coin_layout.view.*
import java.text.FieldPosition

/**
 * Created by romme on 12/03/2018.
 */

class CoinViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    var coinIcon = itemView.coin_icon
    var coinSymbol = itemView.coinSymbol
    var coinName = itemView.coinName
    var coinPrice = itemView.priceUsd
    var oneHourChange = itemView.oneHour
    var twentyFourChange = itemView.twentyFourHour
    var sevenDayChange = itemView.sevenDay
}

class CoinAdapter(recyclerView: RecyclerView,internal var activity: Activity, var items:List<CoinModel>): RecyclerView.Adapter<CoinViewHolder>()
{

    internal var loadMore:ILoadMore?=null
    var isLoading:Boolean=false
    var visibleThreshold=5
    var lastVisibleItem:Int=0
    var totalItemCount:Int=0

    init{
        val linearLayout = recyclerView.layoutManager as LinearLayoutManager
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            totalItemCount = linearLayout.itemCount
            lastVisibleItem = linearLayout.findLastVisibleItemPosition()
            if(!isLoading && totalItemCount <= lastVisibleItem+visibleThreshold)
            {
                if(loadMore != null)
                    loadMore!!.OnLoadMore()
                isLoading = true
            }
        }
    })
    }

    fun setLoadMore(loadMore:ILoadMore)
    {
        this.loadMore = loadMore
    }

    //24:40 in tutorial
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CoinViewHolder {
        val view = LayoutInflater.from(activity).inflate(com.example.romme.kotlincointracker.R.layout.coin_layout,parent,false)
        return CoinViewHolder(view)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CoinViewHolder?, position: Int) {

        val coinModel = items.get(position)

        val item = holder as CoinViewHolder

        item.coinName.text = coinModel.name
        item.coinSymbol.text = coinModel.symbol
        item.coinPrice.text = coinModel.price_usd
        item.oneHourChange.text = coinModel.percent_change_1h+"%"
        item.twentyFourChange.text = coinModel.percent_change_24h+"%"
        item.sevenDayChange.text = coinModel.percent_change_7d+"%"

        Picasso.with(activity.baseContext)
                .load(StringBuilder(Common.imageUrl)
                        .append(coinModel.symbol!!.toLowerCase())
                        .append(".png")
                        .toString())

        //set color
        item.oneHourChange.setTextColor(
                if (coinModel.percent_change_1h!!.contains("-"))
                    Color.parseColor("#FF0000")
        else
                    Color.parseColor("32CD32")
        )

        item.twentyFourChange.setTextColor(
                if (coinModel.percent_change_24h!!.contains("-"))
                    Color.parseColor("#FF0000")
                else
                    Color.parseColor("32CD32")
        )

        item.sevenDayChange.setTextColor(
                if (coinModel.percent_change_7d!!.contains("-"))
                    Color.parseColor("#FF0000")
                else
                    Color.parseColor("32CD32")
        )

    }

    fun setLoaded(){
        isLoading = false
    }

    fun  updateData(coinModels: List<CoinModel>){
        this.items = coinModels
        notifyDataSetChanged()
    }

}

