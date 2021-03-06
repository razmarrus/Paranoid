package com.marvinboots.goodnewseveryone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import java.util.ArrayList
import java.util.HashMap

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.ProgressBar
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
//import android.support.design.widget.FloatingActionButton
//import android.support.design.widget.Snackbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import com.marvinboots.goodnewseveryone.db.NewsItemDao
import com.marvinboots.goodnewseveryone.db.NewsItemsDB
import com.marvinboots.goodnewseveryone.model.NewsItem
import com.prof.rssparser.Article
import com.prof.rssparser.Parser


class MainActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecycleViewAdapter
    private val resultItems = ArrayList<HashMap<String, String>>()
    private var mFeedUrl = "https://www.nasa.gov/rss/dyn/breaking_news.rss"
    private lateinit var progressDialog: ProgressBar
    private var isLoading: Boolean = false
    private lateinit var networkImageView: ImageView
    private lateinit var dao: NewsItemDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.INTERNET),
                    1
                )
        }

        try {
            val editionIntent = intent
            if (editionIntent != null) {
                mFeedUrl = editionIntent.getStringExtra("Url")!!
            }
            mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        } catch (e: Exception) {
            // handler
            mFeedUrl =
                "https://www.nasa.gov/rss/dyn/breaking_news.rss"//"https://www.cbc.ca/cmlink/rss-arts"
        }
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)
        networkImageView = findViewById(R.id.NetImage)

        mAdapter = RecycleViewAdapter(this, resultItems)
        mRecyclerView.setAdapter(mAdapter)

        dao = NewsItemsDB.getInstance(this).newsItemDao()


        if (checkNetWorkState()) {
            for (newsItem in dao.getNewsItems())
                dao.deleteNewsItem(newsItem)
            getDataFromWeb()
        }
        else
            getDataFromRoom()

    }


    inner class GetRssClass(internal var mUrl: String) :
        AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>() {
        init {
            mUrl = mFeedUrl
        }

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg voids: Void): ArrayList<HashMap<String, String>>? {
            var result = ArrayList<HashMap<String, String>>()
            try {
                val url = URL(mUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                val responseCode = connection.responseCode
                println(responseCode)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    result = parseXML(inputStream)
                } else {
                    return null
                }
            } catch (e: Exception) {
                Log.d("Exception", e.message)
                return null
            }

            return result
        }


        override fun onPostExecute(result: ArrayList<HashMap<String, String>>?) {
            super.onPostExecute(result)
            hideProgressDialog()
            if (result == null) {
                showToast("Please check your connection and try again.")
            } else {
                val before = resultItems.size
                resultItems.clear()
                resultItems.addAll(result)
                mAdapter.notifyItemRangeInserted(before, result.size)
                mRecyclerView.invalidate()

            }
        }

        @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
        private fun parseXML(inputStream: InputStream): ArrayList<HashMap<String, String>> {

            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(inputStream)
            val element = document.documentElement

            val itemlist = element.getElementsByTagName("item")
            var itemChildren: NodeList

            var currentItem: Node
            var currentChild: Node

            val items = ArrayList<HashMap<String, String>>()
            var currentMap: HashMap<String, String>?

            var imgCount = 0

            var title = ""
            var description = ""
            var pDate = ""
            var origLinkString = ""
            var imageUrlString = ""


            for (i in 0 until itemlist.length) {

                currentItem = itemlist.item(i)
                itemChildren = currentItem.childNodes
                currentMap = HashMap()

                for (j in 0 until itemChildren.length) {

                    currentChild = itemChildren.item(j)

                    if (currentChild.nodeName.equals("title", ignoreCase = true)) {
                        currentMap["title"] = currentChild.textContent.toString()
                        title = currentMap["title"]!!
                    }

                    if (currentChild.nodeName.equals("description", ignoreCase = true)) {
                        currentMap["description"] = currentChild.textContent.toString()
                        description = currentMap["description"]!!
                    }

                    if (currentChild.nodeName.equals("pubDate", ignoreCase = true)) {
                        currentMap["pubDate"] = currentChild.textContent.toString()
                        pDate = currentMap["pubDate"]!!
                    }

                    if (currentChild.nodeName.equals("feedburner:origLink", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent.toString()
                        origLinkString = currentMap["origLink"]!!
                    }
                    else if (currentChild.nodeName.equals("origLink", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent.toString()
                        origLinkString = currentMap["origLink"]!!
                    }else if (currentChild.nodeName.equals("href", ignoreCase = true)) {
                            currentMap["origLink"] = currentChild.textContent.toString()
                        origLinkString = currentMap["origLink"]!!

                    } else if (currentChild.nodeName.equals("link", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent.toString()
                        origLinkString = currentMap["origLink"]!!
                    }


                    if (currentChild.nodeName.equals("media:thumbnail", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["origLink"]!!
                        }
                    } else if (currentChild.nodeName.equals("media:content", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    } else if (currentChild.nodeName.equals("thumbnail", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    } else if (currentChild.nodeName.equals("enclosure", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    }
                }
                if (currentMap != null && !currentMap.isEmpty()) {
                    items.add(currentMap)
                    var newsItem =
                        NewsItem(title, description, pDate, origLinkString, imageUrlString)
                    dao!!.insertNewsItem(newsItem)
                }
                imgCount = 0
            }
            //loadNewsItems()
            return items
        }
    }


    private fun getDataFromRoom() //: ArrayList<HashMap<String, String>>?
    {
        val items = ArrayList<HashMap<String, String>>()
        for (item in dao.getNewsItems()) {

            var currentMap: HashMap<String, String>?
            currentMap = HashMap()

            currentMap["title"] = item.newsItemTitle
            currentMap["description"] = item.newsItemDescription
            currentMap["pubDate"] = item.newsItemPubDate
            currentMap["origLink"] = item.newsOrigLink
            if (item.newsItemImage != null && item.newsItemImage != "")
                currentMap["imageUrl"] = item.newsItemImage
            //else
            //    currentMap["imageUrl"] = = ""

            if (currentMap != null && !currentMap.isEmpty()) {
                items.add(currentMap)
            }
        }

        if (items == null) {
            showToast("Please check your connection and try again.")
        } else {
            val before = resultItems.size
            resultItems.clear()
            resultItems.addAll( items)
            mAdapter.notifyItemRangeInserted(before,  items.size)
            mRecyclerView.invalidate()

        }
    }


    private fun showProgressDialog() {
        isLoading = true
        progressDialog = ProgressBar(this)//ProgressDialog(this)
        //progressDialog.setCancelable(false)
        //progressDialog.setMessage("Fetching data from web..")
        //progressDialog.show()
    }

    private fun hideProgressDialog() {
        isLoading = false
    }

    private fun getDataFromWeb() {
        val fetchRss = GetRssClass(mFeedUrl)
        fetchRss.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    fun GoHome(view: View)
    {
        val myIntent = Intent(this@MainActivity, GetRSS::class.java)
        myIntent.putExtra("Url", mFeedUrl)
        startActivity(myIntent)
    }

    fun checkInternetStatusButton(view: View)
    {
        finish()
        startActivity(getIntent())
    }

    private fun checkNetWorkState(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        if (activeNetwork == null) {
            /*Toast.makeText(
                this, getString("no inter"),
                Toast.LENGTH_LONG
            ).show()*/
            networkImageView.setImageResource(R.drawable.dino);

            return false
        }
        if (activeNetwork.isConnected) {
            /*Toast.makeText(
                this, getString(R.string.net_connected),
                Toast.LENGTH_LONG
            ).show()*/
            networkImageView.setImageResource(R.drawable.wifi);
            return true
        }
        return false
    }

}
