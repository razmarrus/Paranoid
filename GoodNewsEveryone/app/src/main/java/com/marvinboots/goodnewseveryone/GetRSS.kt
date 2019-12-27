package com.marvinboots.goodnewseveryone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class GetRSS : AppCompatActivity() {

    lateinit var urlLinkEditText: EditText
    lateinit var feedUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_rss_url)

        urlLinkEditText = findViewById(R.id.urlEditText)
        val editionIntent = intent
        var lastUrl = editionIntent.getStringExtra("Url")
        println(lastUrl)
        urlLinkEditText.setText(lastUrl)//text = lastUrl

    }


    fun GoHome(view: View)
    {
        feedUrl = urlLinkEditText.getText().toString()
        val myIntent = Intent(this@GetRSS, MainActivity::class.java)
        myIntent.putExtra("Url", feedUrl)
        startActivity(myIntent)
    }

    fun FirstLinkToEdit(view: View)
    {
        var textView = findViewById(R.id.textView1) as TextView
        urlLinkEditText.setText(textView.getText().toString())
    }

    fun SecondLinkToEdit(view: View)
    {
        var textView = findViewById(R.id.textView2) as TextView
        urlLinkEditText.setText(textView.getText().toString())
    }

    fun ThirdLinkToEdit(view: View)
    {
        var textView = findViewById(R.id.textView3) as TextView
        urlLinkEditText.setText(textView.getText().toString())
    }
}