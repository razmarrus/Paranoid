package com.marvinboots.goodnewseveryone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.marvinboots.goodnewseveryone.model.NewsItem
import com.squareup.picasso.Picasso


class ShowNewsItemActivity : AppCompatActivity() {

    private lateinit var url : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_news_item)

        val newsItemTitle = findViewById(R.id.news_item_title) as TextView
        val newsItemDescription = findViewById(R.id.news_item_description) as TextView
        val newsItemPreviewImage = findViewById(R.id.preview_image) as ImageView
        val editionIntent = intent

        url = editionIntent.getStringExtra("origLink").toString()
        newsItemTitle.setText(editionIntent.getStringExtra("title"))
        newsItemDescription.setText(editionIntent.getStringExtra("description"))

        if (editionIntent.getStringExtra("imageUrl").toString() == null)
            println("bad image")
        println(editionIntent.getStringExtra("imageUrl")!!.toString())
        Picasso.get()//with(mContext)
            .load(editionIntent.getStringExtra("imageUrl")!!.toString())
            .placeholder(R.mipmap.whatastory)
            .resize(328, 328)
            .centerInside()
            .error(R.mipmap.placeholde)
            .into(newsItemPreviewImage)

    }

    fun serfLink(view: View) {
            if (checkNetWorkState()){
            val uri = Uri.parse(url)
            val intent = Intent("margo/Browser")

            intent.setData(Uri.parse(uri.toString()))
            startActivity(intent)
        }
    }


    private fun checkNetWorkState(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        if (activeNetwork == null) {
            //Toast.makeText(ShowNewsItemActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
            return false
        }
        if (activeNetwork.isConnected) {
            return true
        }
        return false
    }

}
