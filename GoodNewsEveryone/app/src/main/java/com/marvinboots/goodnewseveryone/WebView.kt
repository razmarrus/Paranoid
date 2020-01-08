package com.marvinboots.goodnewseveryone

import android.net.Uri;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent.getIntent
import androidx.appcompat.app.AppCompatActivity
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.net.http.*;



class WebView : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.web_view)

            val url = intent.data

            //val editionIntent = intent
            //var url = editionIntent.getStringExtra("Url")
            val webView = findViewById(R.id.webView) as WebView
            webView.getSettings().setJavaScriptEnabled(true);
            //val webCl = webViewClient()
            //webView.webViewClient = webViewClient()
            webView.setWebViewClient(Callback())
            webView.loadUrl(url!!.toString())
        }

        private inner class Callback : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

    }