package com.example.fix_finder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fix_finder.data.model.NotificationItem

class NotificationsAdapter(
    private var notifications: List<NotificationItem>,
    private val onItemClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    fun updateData(newNotifications: List<NotificationItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = notifications.size
            override fun getNewListSize(): Int = newNotifications.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return notifications[oldItemPosition].id == newNotifications[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return notifications[oldItemPosition] == newNotifications[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.notifications = newNotifications
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]
        holder.bind(item, onItemClick)
    }

    override fun getItemCount(): Int = notifications.size

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgIcon: ImageView = itemView.findViewById(R.id.imgNotifIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNotifTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvNotifMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvNotifTime)
        private val viewUnreadDot: View = itemView.findViewById(R.id.viewUnreadDot)

        fun bind(item: NotificationItem, onItemClick: (NotificationItem) -> Unit) {
            tvTitle.text = item.title
            tvMessage.text = item.message
            tvTime.text = item.timestamp
            viewUnreadDot.visibility = if (item.isRead) View.GONE else View.VISIBLE

            when (item.type) {
                NotificationItem.TYPE_BOOKING -> {
                    imgIcon.setImageResource(R.drawable.ic_check)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DCFCE7"))
                    imgIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#15803D"))
                }
                NotificationItem.TYPE_PROMO -> {
                    imgIcon.setImageResource(R.drawable.ic_star)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF3C7"))
                    imgIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#B45309"))
                }
                NotificationItem.TYPE_SYSTEM -> {
                    imgIcon.setImageResource(R.drawable.ic_clock)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DBEAFE"))
                    imgIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#1D4ED8"))
                }
                else -> {
                    imgIcon.setImageResource(R.drawable.ic_notification)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0E7FF"))
                    imgIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#4338CA"))
                }
            }

            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
