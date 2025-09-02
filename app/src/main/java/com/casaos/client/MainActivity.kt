package com.casaos.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.casaos.client.data.ConnectionStatus
import com.casaos.client.databinding.ActivityMainBinding
import com.casaos.client.network.NetworkManager
import com.casaos.client.utils.SettingsManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager
    private lateinit var networkManager: NetworkManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        settingsManager = SettingsManager.getInstance(this)
        networkManager = NetworkManager.getInstance()
        
        setupWebView()
        setupUI()
        
        // Load configuration and connect
        loadConfigAndConnect()
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.swipeRefreshLayout.isRefreshing = false
                    hideLoading()
                }
                
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    binding.swipeRefreshLayout.isRefreshing = false
                    hideLoading()
                    showConnectionError("Failed to load page: ${error?.description}")
                }
                
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    // Allow navigation within the same domain
                    val url = request?.url?.toString() ?: return false
                    val baseUrl = networkManager.getWebUrl() ?: return true
                    
                    return if (url.startsWith(baseUrl)) {
                        false // Let WebView handle it
                    } else {
                        true // Block external navigation
                    }
                }
            }
            
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        hideLoading()
                    }
                }
            }
        }
    }
    
    private fun setupUI() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshWebView()
        }
        
        binding.settingsButton.setOnClickListener {
            openSettings()
        }
        
        binding.retryButton.setOnClickListener {
            loadConfigAndConnect()
        }
    }
    
    private fun loadConfigAndConnect() {
        val config = settingsManager.loadConfig()
        
        if (!config.isValid()) {
            showConnectionScreen(
                getString(R.string.not_connected),
                getString(R.string.configure_connection_message)
            )
            return
        }
        
        showLoading()
        
        lifecycleScope.launch {
            try {
                val status = networkManager.login(config)
                handleConnectionResult(status)
            } catch (e: Exception) {
                hideLoading()
                showConnectionError("Connection failed: ${e.message}")
            }
        }
    }
    
    private fun handleConnectionResult(status: ConnectionStatus) {
        if (status.isConnected) {
            showWebView()
            loadWebPage()
        } else {
            showConnectionError(status.errorMessage ?: "Unknown error")
        }
    }
    
    private fun loadWebPage() {
        val webUrl = networkManager.getWebUrl()
        if (webUrl != null) {
            binding.webView.loadUrl(webUrl)
        } else {
            showConnectionError("Invalid server configuration")
        }
    }
    
    private fun refreshWebView() {
        if (binding.webView.visibility == View.VISIBLE) {
            binding.webView.reload()
        } else {
            loadConfigAndConnect()
        }
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.connectionLayout.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.GONE
    }
    
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
    
    private fun showWebView() {
        binding.progressBar.visibility = View.GONE
        binding.connectionLayout.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.VISIBLE
    }
    
    private fun showConnectionScreen(title: String, message: String) {
        binding.progressBar.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.connectionLayout.visibility = View.VISIBLE
        binding.connectionStatusText.text = title
        binding.connectionMessageText.text = message
    }
    
    private fun showConnectionError(error: String) {
        showConnectionScreen(
            getString(R.string.connection_failed),
            error
        )
    }
    
    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        // Reload configuration when returning from settings
        if (settingsManager.hasConfig() && binding.webView.visibility != View.VISIBLE) {
            loadConfigAndConnect()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                refreshWebView()
                true
            }
            R.id.action_settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webView.canGoBack() && binding.webView.visibility == View.VISIBLE) {
            binding.webView.goBack()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}