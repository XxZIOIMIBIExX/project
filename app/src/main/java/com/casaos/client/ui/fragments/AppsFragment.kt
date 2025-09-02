package com.casaos.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casaos.client.R
import com.casaos.client.data.AppInfo
import com.casaos.client.network.NetworkManager
import com.casaos.client.ui.adapters.AppsAdapter
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class AppsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingCard: MaterialCardView
    private lateinit var appsAdapter: AppsAdapter
    
    private val networkManager = NetworkManager.getInstance()
    private val apps = mutableListOf<AppInfo>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_apps, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerView()
        loadApps()
    }
    
    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.appsRecyclerView)
        loadingCard = view.findViewById(R.id.loadingCard)
    }
    
    private fun setupRecyclerView() {
        appsAdapter = AppsAdapter(apps) { app, action ->
            handleAppAction(app, action)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appsAdapter
        }
    }
    
    private fun loadApps() {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val response = networkManager.getApps()
                if (response.isSuccessful && response.body()?.success == true) {
                    val appsList = response.body()?.data ?: emptyList()
                    updateApps(appsList)
                    showContent()
                } else {
                    showError("Failed to load applications")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }
    
    private fun updateApps(newApps: List<AppInfo>) {
        apps.clear()
        apps.addAll(newApps)
        appsAdapter.notifyDataSetChanged()
    }
    
    private fun handleAppAction(app: AppInfo, action: String) {
        lifecycleScope.launch {
            try {
                val response = when (action) {
                    "start" -> networkManager.startApp(app.id)
                    "stop" -> networkManager.stopApp(app.id)
                    "restart" -> networkManager.restartApp(app.id)
                    else -> return@launch
                }
                
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "Action completed successfully", Toast.LENGTH_SHORT).show()
                    // Refresh the app list
                    loadApps()
                } else {
                    Toast.makeText(context, "Action failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showLoading() {
        loadingCard.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
    
    private fun showContent() {
        loadingCard.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
    
    private fun showError(message: String) {
        showContent()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    fun refresh() {
        loadApps()
    }
}