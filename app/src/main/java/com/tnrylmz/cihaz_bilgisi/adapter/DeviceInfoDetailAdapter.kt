package com.tnrylmz.cihaz_bilgisi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tnrylmz.cihaz_bilgisi.R
import com.tnrylmz.cihaz_bilgisi.model.DeviceInfoItem

class DeviceInfoDetailAdapter(
    private val items: List<DeviceInfoItem>
) : RecyclerView.Adapter<DeviceInfoDetailAdapter.DetailViewHolder>() {

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelText: TextView = itemView.findViewById(R.id.labelText)
        val valueText: TextView = itemView.findViewById(R.id.valueText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_info_detail, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items[position]
        holder.labelText.text = item.label
        holder.valueText.text = item.value
    }

    override fun getItemCount(): Int = items.size
}