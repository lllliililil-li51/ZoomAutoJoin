package com.example.zoomautojoin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zoomautojoin.databinding.ItemScheduleBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ScheduleAdapter(
    private val items: MutableList<ClassSchedule>,
    private val onDelete: (ClassSchedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val fmt = SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault())
        holder.binding.itemTitle.text = item.title
        holder.binding.itemTime.text = fmt.format(item.timeInMillis) +
            if (item.repeatWeekly) "  (weekly)" else ""
        holder.binding.itemLink.text = item.zoomLink
        holder.binding.deleteButton.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ClassSchedule>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
