package com.casaos.client.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.casaos.client.R
import com.casaos.client.data.AppInfo
import com.casaos.client.data.AppStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class AppsAdapter(
    private val apps: List<AppInfo>,
    private val onAppAction: (AppInfo, String) -> Unit
) : RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.appCard)
        val icon: ImageView = itemView.findViewById(R.id.appIcon)
        val name: MaterialTextView = itemView.findViewById(R.id.appName)
        val description: MaterialTextView = itemView.findViewById(R.id.appDescription)
        val status: MaterialTextView = itemView.findViewById(R.id.appStatus)
        val actionButton: MaterialButton = itemView.findViewById(R.id.actionButton)
        val menuButton: MaterialButton = itemView.findViewById(R.id.menuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        
        holder.name.text = app.name
        holder.description.text = app.description
        
        // Set status and action button based on app state
        when (app.status) {
            AppStatus.RUNNING -> {
                holder.status.text = "Running"
                holder.status.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
                holder.actionButton.text = "Stop"
                holder.actionButton.setOnClickListener {
                    onAppAction(app, "stop")
                }
            }
            AppStatus.STOPPED -> {
                holder.status.text = "Stopped"
                holder.status.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
                holder.actionButton.text = "Start"
                holder.actionButton.setOnClickListener {
                    onAppAction(app, "start")
                }
            }
            else -> {
                holder.status.text = app.status.name
                holder.status.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
                holder.actionButton.text = "Start"
                holder.actionButton.setOnClickListener {
                    onAppAction(app, "start")
                }
            }
        }
        
        // Setup menu button
        holder.menuButton.setOnClickListener { view ->
            showPopupMenu(view, app)
        }
        
        // Set app icon (placeholder for now)
        holder.icon.setImageResource(R.drawable.ic_apps)
    }

    override fun getItemCount(): Int = apps.size

    private fun showPopupMenu(view: View, app: AppInfo) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.app_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_start -> {
                    onAppAction(app, "start")
                    true
                }
                R.id.action_stop -> {
                    onAppAction(app, "stop")
                    true
                }
                R.id.action_restart -> {
                    onAppAction(app, "restart")
                    true
                }
                R.id.action_terminal -> {
                    onAppAction(app, "terminal")
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }
}