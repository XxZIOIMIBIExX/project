package com.casaos.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.casaos.client.R
import com.casaos.client.data.SystemInfo
import com.casaos.client.network.NetworkManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class StatusFragment : Fragment() {
    
    private lateinit var cpuUsageBar: LinearProgressIndicator
    private lateinit var memoryUsageBar: LinearProgressIndicator
    private lateinit var diskUsageBar: LinearProgressIndicator
    
    private lateinit var cpuUsageText: MaterialTextView
    private lateinit var memoryUsageText: MaterialTextView
    private lateinit var diskUsageText: MaterialTextView
    
    private lateinit var cpuInfoText: MaterialTextView
    private lateinit var memoryInfoText: MaterialTextView
    private lateinit var diskInfoText: MaterialTextView
    private lateinit var uptimeText: MaterialTextView
    private lateinit var versionText: MaterialTextView
    
    private lateinit var loadingCard: MaterialCardView
    private lateinit var contentCard: MaterialCardView
    
    private val networkManager = NetworkManager.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        loadSystemInfo()
    }
    
    private fun initViews(view: View) {
        // Progress bars
        cpuUsageBar = view.findViewById(R.id.cpuUsageBar)
        memoryUsageBar = view.findViewById(R.id.memoryUsageBar)
        diskUsageBar = view.findViewById(R.id.diskUsageBar)
        
        // Usage text
        cpuUsageText = view.findViewById(R.id.cpuUsageText)
        memoryUsageText = view.findViewById(R.id.memoryUsageText)
        diskUsageText = view.findViewById(R.id.diskUsageText)
        
        // Info text
        cpuInfoText = view.findViewById(R.id.cpuInfoText)
        memoryInfoText = view.findViewById(R.id.memoryInfoText)
        diskInfoText = view.findViewById(R.id.diskInfoText)
        uptimeText = view.findViewById(R.id.uptimeText)
        versionText = view.findViewById(R.id.versionText)
        
        // Cards
        loadingCard = view.findViewById(R.id.loadingCard)
        contentCard = view.findViewById(R.id.contentCard)
    }
    
    private fun loadSystemInfo() {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val response = networkManager.getSystemInfo()
                if (response.isSuccessful && response.body()?.success == true) {
                    val systemInfo = response.body()?.data
                    if (systemInfo != null) {
                        updateUI(systemInfo)
                        showContent()
                    } else {
                        showError("No system information available")
                    }
                } else {
                    showError("Failed to load system information")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }
    
    private fun updateUI(systemInfo: SystemInfo) {
        // Update CPU info
        cpuUsageBar.progress = systemInfo.cpu.usage.toInt()
        cpuUsageText.text = "${systemInfo.cpu.usage.toInt()}%"
        cpuInfoText.text = "${systemInfo.cpu.model} (${systemInfo.cpu.cores} cores)"
        
        // Update Memory info
        memoryUsageBar.progress = systemInfo.memory.usagePercent.toInt()
        memoryUsageText.text = "${systemInfo.memory.usagePercent.toInt()}%"
        memoryInfoText.text = "${formatBytes(systemInfo.memory.used)} / ${formatBytes(systemInfo.memory.total)}"
        
        // Update Disk info
        diskUsageBar.progress = systemInfo.disk.usagePercent.toInt()
        diskUsageText.text = "${systemInfo.disk.usagePercent.toInt()}%"
        diskInfoText.text = "${formatBytes(systemInfo.disk.used)} / ${formatBytes(systemInfo.disk.total)}"
        
        // Update system info
        uptimeText.text = formatUptime(systemInfo.uptime)
        versionText.text = systemInfo.version
    }
    
    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return "%.1f %s".format(size, units[unitIndex])
    }
    
    private fun formatUptime(seconds: Long): String {
        val days = seconds / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        
        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
    
    private fun showLoading() {
        loadingCard.visibility = View.VISIBLE
        contentCard.visibility = View.GONE
    }
    
    private fun showContent() {
        loadingCard.visibility = View.GONE
        contentCard.visibility = View.VISIBLE
    }
    
    private fun showError(message: String) {
        loadingCard.visibility = View.GONE
        contentCard.visibility = View.VISIBLE
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    fun refresh() {
        loadSystemInfo()
    }
}