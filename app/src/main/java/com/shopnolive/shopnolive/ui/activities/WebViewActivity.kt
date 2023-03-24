package com.shopnolive.shopnolive.ui.activities

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.databinding.ActivityWebViewBinding
import com.shopnolive.shopnolive.utils.isNetworkAvailable
import com.shopnolive.shopnolive.utils.toast

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    private var title = ""
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra("title").toString()
        //url = intent.getStringExtra("url").toString()

        //get data from intent
        setSupportActionBar(binding.toolbarLayout.toolbar)
        binding.toolbarLayout.toolbar.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        url = if (title == getString(R.string.privacy_policy)) {
            "file:///android_asset/privacy_policy.html"
        } else {
            "file:///android_asset/terms_and_conditions.html"
        }

        loadWebView()
    }

    private fun checkConnection() {
        if (isNetworkAvailable()) {
            loadWebView()
        } else {
            toast("No Internet Connection")
        }
    }

    private fun loadWebView() {
        binding.webView.loadUrl(url)
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressbar.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                toast(description)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}