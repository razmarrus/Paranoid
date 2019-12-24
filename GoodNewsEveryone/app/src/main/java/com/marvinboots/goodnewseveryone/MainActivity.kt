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
import android.content.Intent
import android.widget.ProgressBar
import android.os.AsyncTask
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
//import android.support.design.widget.FloatingActionButton
//import android.support.design.widget.Snackbar


import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView //? = null
    private lateinit var mAdapter: RecycleViewAdapter //? = null
    private val resultItems = ArrayList<HashMap<String, String>>()
    private val mFeedUrl = "https://www.cbc.ca/cmlink/rss-arts"//"http://rss.cnn.com/rss/edition.rss"//"http://feeds.feedburner.com/techcrunch/android?format=xml"
    private lateinit var progressDialog: ProgressBar //= null
    private var isLoading: Boolean = false

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

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        /*val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(view, "Rss Feed Url: $mFeedUrl", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        })*/

        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView

        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)

        mAdapter = RecycleViewAdapter(this, resultItems)
        mRecyclerView.setAdapter(mAdapter)

        getDataFromWeb()

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

            for (i in 0 until itemlist.length) {

                currentItem = itemlist.item(i)
                itemChildren = currentItem.childNodes

                currentMap = HashMap()

                for (j in 0 until itemChildren.length) {

                    currentChild = itemChildren.item(j)

                    if (currentChild.nodeName.equals("title", ignoreCase = true)) {
                        // Log.d("Title", String.valueOf(currentChild.getTextContent()));
                        currentMap["title"] = currentChild.textContent
                    }

                    if (currentChild.nodeName.equals("description", ignoreCase = true)) {
                        // Log.d("description", String.valueOf(currentChild.getTextContent()));
                        currentMap["description"] = currentChild.textContent
                    }

                    if (currentChild.nodeName.equals("pubDate", ignoreCase = true)) {
                        // Log.d("Title", String.valueOf(currentChild.getTextContent()));
                        currentMap["pubDate"] = currentChild.textContent
                    }

                    if (currentChild.nodeName.equals("feedburner:origLink", ignoreCase = true)) {
                        currentMap["origLink"] = currentChild.textContent
                    }

                    if (currentChild.nodeName.equals("media:thumbnail", ignoreCase = true)) {
                        imgCount++

                        if (imgCount == 1) {
                            currentMap["imageUrl"] = currentChild.attributes.item(0).textContent
                        }
                    }
                }
                if (currentMap != null && !currentMap.isEmpty()) {
                    items.add(currentMap)
                }
                imgCount = 0
            }

            return items

        }
    }

    private fun showProgressDialog() {
        isLoading = true
        //progressDialog = ProgressBar(this)//ProgressDialog(this)
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

    fun GoHome()
    {

        val myIntent = Intent(this@MainActivity, GetRSS::class.java)
        myIntent.putExtra("Url", mFeedUrl)
        startActivity(intent)
    }

    fun GoDashboard()
    {
    }

    fun GoViewed()
    {
        val myIntent = Intent(this@MainActivity, GetRSS::class.java)
        myIntent.putExtra("Url", mFeedUrl)
        startActivity(intent)

    }
}
