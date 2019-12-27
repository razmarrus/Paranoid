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
import com.marvinboots.goodnewseveryone.model.NewAdapter
import com.marvinboots.goodnewseveryone.model.NewsItem


class MainActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView //? = null
   // private lateinit var mAdapter: RecycleViewAdapter //? = null
    private lateinit var nAdapter: NewAdapter
    private val resultItems = ArrayList<HashMap<String, String>>()
    private var mFeedUrl = "https://www.nasa.gov/rss/dyn/breaking_news.rss"//"https://news.tut.by/rss.html"//"http://rss.cnn.com/rss/edition.rss"//"http://feeds.feedburner.com/techcrunch/android?format=xml"
    private lateinit var progressDialog: ProgressBar //= null
    private var isLoading: Boolean = false
    private lateinit var networkImageView : ImageView
    private var dao: NewsItemDao? = null
    //private var newsItems: ArrayList<NewsItem>()//? = null
    private var newsItems = arrayListOf<NewsItem>()


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
        }
        catch (e: Exception) {
            // handler
            mFeedUrl = "https://www.nasa.gov/rss/dyn/breaking_news.rss"//"https://www.cbc.ca/cmlink/rss-arts"
        }
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)
        networkImageView = findViewById(R.id.NetImage)


        nAdapter = NewAdapter(this, newsItems)
        mRecyclerView.setAdapter(nAdapter)


        //mAdapter = RecycleViewAdapter(this, resultItems)
        //mRecyclerView.setAdapter(mAdapter)
        dao = NewsItemsDB.getInstance(this).newsItemDao()
        if(checkNetWorkState())
        {
            for (newsItem in dao!!.getNewsItems()) {
                dao!!.deleteNewsItem(newsItem)
            }
            /*for (article in articles) {
                dao!!.insertNewsItem(RssUtils.articleToNewsItem(article))
            }*/
            //loadNewsItems()
            getDataFromWeb()
        }

        //getDataFromWeb()

    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_exit -> finish()
            R.id.refresh -> if (!isLoading) {
                getDataFromWeb()
            } else {
                showToast("Please wait..")
            }
        }
        return super.onOptionsItemSelected(item)
    }*/


    inner class GetRssClass(internal var mUrl: String) :
        AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>()
    {
        init{
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
                val before = resultItems.size //newsItems!!.size//
                resultItems.clear()
                resultItems.addAll(result)

                //mAdapter.notifyItemRangeInserted(before, result.size)
                //mRecyclerView.invalidate()


                newsItems = ArrayList()
                val list = dao!!.getNewsItems()
                newsItems.addAll(list)
                nAdapter.notifyItemRangeInserted(before, newsItems.size)
                //nAdapter = NewAdapter(this, newsItems)//resultItems)
                mRecyclerView.setAdapter(nAdapter)
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
                        // Log.d("Title", String.valueOf(currentChild.getTextContent()));
                        currentMap["title"] = currentChild.textContent.toString()
                        title =  currentMap["title"]!!

                        //newsItem.newsItemTitle("qwe")
                    }

                    if (currentChild.nodeName.equals("description", ignoreCase = true)) {
                        // Log.d("description", String.valueOf(currentChild.getTextContent()));
                        currentMap["description"] = currentChild.textContent.toString()
                        description = currentMap["description"]!!
                    }

                    if (currentChild.nodeName.equals("pubDate", ignoreCase = true)) {
                        // Log.d("Title", String.valueOf(currentChild.getTextContent()));
                        currentMap["pubDate"] = currentChild.textContent.toString()
                        pDate = currentMap["pubDate"]!!
                    }

                    if (currentChild.nodeName.equals("feedburner:origLink", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent
                        origLinkString =   currentMap["origLink"]!!
                    }
                    else if (currentChild.nodeName.equals("origLink", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent
                        origLinkString =   currentMap["origLink"]!!
                    }
                    else if (currentChild.nodeName.equals("link", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent
                        origLinkString  = currentMap["imageUrl"]!!
                    }

                    if (currentChild.nodeName.equals("media:thumbnail", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    }
                    else if (currentChild.nodeName.equals("media:content", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    }
                    else if (currentChild.nodeName.equals("thumbnail", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    }
                    else if (currentChild.nodeName.equals("enclosure", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                            imageUrlString = currentMap["imageUrl"]!!
                        }
                    }
                    /*if(getImage(currentChild.attributes.item(0).textContent) != null)
                    {
                        currentMap["imageUrl"] = currentChild.attributes.item(0).textContent//getImage(currentChild.attributes.item(0).textContent)
                    }*/
                    //dao!!.insertNewsItem(RssUtils.articleToNewsItem(article)
                    if(title != "" && description != "" && origLinkString != "" &&  pDate != "" &&  imageUrlString != "") {
                        var newsItem =
                            NewsItem(title, description, pDate, origLinkString, imageUrlString)

                            dao!!.insertNewsItem(newsItem)
                    }
                    println("mewo")
                }
                if (currentMap != null && !currentMap.isEmpty()) {
                    items.add(currentMap)
                }
                imgCount = 0
            }
            //loadNewsItems()
            return items

        }
    }

    /*fun loadNewsItems() {
        this.newsItems = ArrayList()
        val list = dao!!.getNewsItems()
        this.newsItems!!.addAll(list)

        mAdapter = RecycleViewAdapter(this, newsItems)//resultItems)
        mRecyclerView.setAdapter(mAdapter)
        //val adapter = NewsItemAdapter(this, newsItems)
        //adapter.setListener(this)
        //recyclerView.setAdapter(adapter)
    }*/



    private fun showProgressDialog() {
        isLoading = true
        progressDialog = ProgressBar(this)//ProgressDialog(this)
        //progressDialog.setCancelable(false)
        //progressDialog.setMessage("Fetching data from web..")
        //progressDialog.show()
    }

    private fun hideProgressDialog() {
        isLoading = false
        /*if (progressDialog != null) {
            progressDialog.cancel()
        }*/
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
