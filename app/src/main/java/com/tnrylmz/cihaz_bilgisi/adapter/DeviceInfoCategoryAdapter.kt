package com.tnrylmz.cihaz_bilgisi.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tnrylmz.cihaz_bilgisi.R
import com.tnrylmz.cihaz_bilgisi.model.DeviceInfoCategory

class DeviceInfoCategoryAdapter(
    private val categories: MutableList<DeviceInfoCategory>
) : RecyclerView.Adapter<DeviceInfoCategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        val expandIcon: ImageView = itemView.findViewById(R.id.expandIcon)
        val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_info_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        
        holder.categoryTitle.text = category.title
        holder.categoryIcon.setImageResource(category.iconRes)
        
        // Set icon tint color
        holder.categoryIcon.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, category.backgroundColor)
        )
        
        // Setup nested RecyclerView
        val detailAdapter = DeviceInfoDetailAdapter(category.items)
        holder.itemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailAdapter
            isNestedScrollingEnabled = false
        }
        
        // Handle expand/collapse
        updateExpandState(holder, category.isExpanded)
        
        holder.itemView.setOnClickListener {
            category.isExpanded = !category.isExpanded
            updateExpandState(holder, category.isExpanded)
        }
    }
    
    private fun updateExpandState(holder: CategoryViewHolder, isExpanded: Boolean) {
        val rotation = if (isExpanded) 180f else 0f
        ObjectAnimator.ofFloat(holder.expandIcon, "rotation", rotation).start()
        
        holder.itemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = categories.size
}