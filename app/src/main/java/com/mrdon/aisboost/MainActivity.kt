package com.mrdon.aisboost

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {
    private lateinit var web: WebView
    private val host = "aistudio.google.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val boost = assets.open("boost.js").bufferedReader().use { it.readText() }

        web = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                // Chỉ cho phép điều hướng trong các miền Google; link khác bị chặn.
                override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                    val h = r.url.host ?: return true
                    return !(h.endsWith("google.com") || h.endsWith("gstatic.com"))
                }
                override fun onPageStarted(v: WebView, url: String, f: Bitmap?) {
                    if (Uri(url).host == host) v.evaluateJavascript(boost, null)
                }
                override fun onPageFinished(v: WebView, url: String) {
                    if (Uri(url).host == host) v.evaluateJavascript(boost, null)
                }
            }
            loadUrl("https://$host/")
        }
        setContentView(web)
    }

    private fun Uri(s: String) = android.net.Uri.parse(s)

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
