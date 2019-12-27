package com.marvinboots.goodnewseveryone.model

import com.marvinboots.goodnewseveryone.R

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import java.util.HashMap
import androidx.cardview.widget.CardView
import com.squareup.picasso.Picasso

class NewAdapter( context: Context,  dataset : ArrayList<NewsItem>) : RecyclerView.Adapter<NewAdapter.ViewHolder>() {
    //private var mDataset = ArrayList<HashMap<String, String>>()
    lateinit var mContext: Context
    private var lastPosition = -1
    private var mDataset : ArrayList<NewsItem>? = null

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_title: TextView
        var tv_category: TextView
        var tv_description: TextView
        var tv_date: TextView
        var iv_image: ImageView
        //var iv_fav: ImageView
        //var iv_share: ImageView
        var cardView: CardView


        init {
            tv_title = v.findViewById(R.id.title) as TextView
            tv_category = v.findViewById(R.id.category) as TextView
            tv_description = v.findViewById(R.id.description) as TextView
            tv_date = v.findViewById(R.id.date) as TextView

            iv_image = v.findViewById(R.id.image) as ImageView
            // iv_fav = v.findViewById(R.id.fav) as ImageView
            //iv_share = v.findViewById(R.id.share) as ImageView

            cardView = v.findViewById(R.id.card_view) as CardView

        }
    }

    init{
        mContext = context
        mDataset = dataset
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // position should never be final

        lastPosition = position
        var newsItem = mDataset!![position]// : NewsItem
        //val map = mDataset[position]

        val imageurl = newsItem.getNewsItemImage()
        println("\n\nImageUrl" + imageurl)
        Picasso.get()//with(mContext)
            .load(imageurl)
            .placeholder(R.mipmap.whatastory)
            .resize(dpToPx(128), dpToPx(128))
            .centerInside()
            .error(R.mipmap.placeholde)
            .into(holder.iv_image)

        holder.tv_title.text = newsItem.getNewsItemTitle()

        val fromHtml = newsItem.newsItemDescription
        val lastStr = fromHtml.substring(2, fromHtml.length - 1)

        holder.tv_description.text = lastStr.trim { it <= ' ' }
        holder.tv_date.text = newsItem.newsItemPubDate


        holder.cardView.setOnClickListener(onCardClick(holder.getAdapterPosition()))

        //holder.iv_share.setOnClickListener(onPostShare(holder.getAdapterPosition()))

        //holder.iv_fav.setOnClickListener(onAddfav(holder.getAdapterPosition()))

    }


    internal inner class onCardClick(var position: Int) : View.OnClickListener {

        override fun onClick(view: View) {
            var map = mDataset!![position]
            //println(map["origLink"])
            Log.d("Position", map.newsOrigLink)
            val uri = Uri.parse( map.newsOrigLink)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            mContext.startActivity(intent)
        }
    }


    internal inner class onAddfav(var position: Int) : View.OnClickListener {
        override fun onClick(view: View) {
            Toast.makeText(mContext, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
    }


    override fun getItemCount(): Int {
        return mDataset!!.size
    }


    fun dpToPx(dp: Int): Int {
        val displayMetrics = mContext.resources
            .displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}