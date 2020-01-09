package com.marvinboots.goodnewseveryone

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
import android.Manifest
import android.net.ConnectivityManager
import android.net.http.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.marvinboots.goodnewseveryone.ShowNewsItemActivity


class RecycleViewAdapter( context: Context,  dataset : ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {
    private var mDataset = ArrayList<HashMap<String, String>>()
    lateinit var mContext: Context
    private var lastPosition = -1

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_title: TextView
        //var tv_description: TextView
        var tv_date: TextView
        var iv_image: ImageView
        var cardView: CardView


        init {
            tv_title = v.findViewById(R.id.title) as TextView
            //tv_description = v.findViewById(R.id.description) as TextView
            tv_date = v.findViewById(R.id.date) as TextView
            iv_image = v.findViewById(R.id.image) as ImageView
            cardView = v.findViewById(R.id.card_view) as CardView

        }
    }

    init{
        mContext = context
        mDataset = dataset
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // position should never be final

        lastPosition = position

        val map = mDataset[position]

        val imageurl = map["imageUrl"]
        println("\n\nImageUrl" + imageurl)
        Picasso.get()//with(mContext)
            .load(imageurl)
            .placeholder(R.mipmap.whatastory)
            .resize(dpToPx(128), dpToPx(128))
            .centerInside()
            .error(R.mipmap.placeholde)
            .into(holder.iv_image)

        holder.tv_title.text = map["title"]

        val fromHtml = Html.fromHtml(map["description"]).toString()
        val lastStr = fromHtml.substring(2, fromHtml.length - 1)

        //holder.tv_description.text = lastStr.trim { it <= ' ' }
        holder.tv_date.text = map["pubDate"]

        holder.cardView.setOnClickListener(onCardClick(holder.getAdapterPosition()))

    }


    internal inner class onCardClick(var position: Int) : View.OnClickListener {

        override fun onClick(view: View) {
            val map = mDataset[position]
            println(map["origLink"])

            val intent = Intent("NoteView")
            println(map["origLink"].toString() + map["title"].toString())
            intent.putExtra("origLink", map["origLink"].toString())
            intent.putExtra("imageUrl", map["imageUrl"].toString())
            intent.putExtra("title", map["title"].toString())
            intent.putExtra("description", map["description"].toString())
            mContext.startActivity(intent)

            /*if(map["origLink"] != null && map["origLink"] != "" )
            {
                Log.d("Position", map["origLink"]!!)
                //val uri = Uri.parse( map.newsOrigLink)
                println(map["origLink"])
                val uri = Uri.parse(map["origLink"]!!)
                val intent = Intent("margo/Browser")
                //val intent = Intent(this@RecycleViewAdapter, WebView::class.java)
                    //intent.putExtra("Url", uri.toString())//feedUrl)
                intent.setData(Uri.parse(uri.toString()))
                mContext.startActivity(intent)
            }*/
        }
    }


    override fun getItemCount(): Int {
        return mDataset.size
    }


    fun dpToPx(dp: Int): Int {
        val displayMetrics = mContext.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }


}