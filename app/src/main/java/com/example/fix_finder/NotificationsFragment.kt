package com.example.fix_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fix_finder.data.model.NotificationItem

class NotificationsFragment : Fragment() {

    private lateinit var notificationsAdapter: NotificationsAdapter
    private val notificationList = mutableListOf<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSampleNotifications()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnMarkAllRead = view.findViewById<TextView>(R.id.btnMarkAllRead)
        val btnClearNotifications = view.findViewById<TextView>(R.id.btnClearNotifications)
        val tvUnreadCountHeader = view.findViewById<TextView>(R.id.tvUnreadCountHeader)
        val rvNotifications = view.findViewById<RecyclerView>(R.id.rvNotifications)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layoutNotificationsEmpty)

        btnBack?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        notificationsAdapter = NotificationsAdapter(notificationList) { selectedItem ->
            selectedItem.isRead = true
            notificationsAdapter.notifyDataSetChanged()
            updateHeader(tvUnreadCountHeader, layoutEmpty, rvNotifications)
            Toast.makeText(context, selectedItem.title, Toast.LENGTH_SHORT).show()
        }

        rvNotifications?.layoutManager = LinearLayoutManager(requireContext())
        rvNotifications?.adapter = notificationsAdapter

        btnMarkAllRead?.setOnClickListener {
            notificationList.forEach { it.isRead = true }
            notificationsAdapter.notifyDataSetChanged()
            updateHeader(tvUnreadCountHeader, layoutEmpty, rvNotifications)
            Toast.makeText(context, "All notifications marked as read.", Toast.LENGTH_SHORT).show()
        }

        btnClearNotifications?.setOnClickListener {
            notificationList.clear()
            notificationsAdapter.updateData(emptyList())
            updateHeader(tvUnreadCountHeader, layoutEmpty, rvNotifications)
            Toast.makeText(context, "Notifications cleared.", Toast.LENGTH_SHORT).show()
        }

        updateHeader(tvUnreadCountHeader, layoutEmpty, rvNotifications)
    }

    private fun updateHeader(tvHeader: TextView?, layoutEmpty: LinearLayout?, rv: RecyclerView?) {
        val unreadCount = notificationList.count { !it.isRead }
        if (notificationList.isEmpty()) {
            layoutEmpty?.visibility = View.VISIBLE
            rv?.visibility = View.GONE
            tvHeader?.text = "No Notifications"
        } else {
            layoutEmpty?.visibility = View.GONE
            rv?.visibility = View.VISIBLE
            tvHeader?.text = if (unreadCount > 0) "$unreadCount Unread Notification${if (unreadCount > 1) "s" else ""}" else "All Caught Up"
        }
    }

    private fun loadSampleNotifications() {
        if (notificationList.isNotEmpty()) return
        notificationList.addAll(
            listOf(
                NotificationItem(
                    id = "n_1",
                    title = "Request Accepted!",
                    message = "Thabo Mokoena has accepted your Tap Replacement request (#REQ-1001).",
                    timestamp = "10 mins ago",
                    isRead = false,
                    type = NotificationItem.TYPE_BOOKING
                ),
                NotificationItem(
                    id = "n_2",
                    title = "Technician Arriving Soon",
                    message = "Lerato Ndlovu is scheduled for your Electrical inspection today at 2:00 PM.",
                    timestamp = "1 hour ago",
                    isRead = false,
                    type = NotificationItem.TYPE_SYSTEM
                ),
                NotificationItem(
                    id = "n_3",
                    title = "Special Offer 🛠️",
                    message = "Get 15% off your next Air Conditioning service this weekend in Polokwane!",
                    timestamp = "Yesterday",
                    isRead = true,
                    type = NotificationItem.TYPE_PROMO
                ),
                NotificationItem(
                    id = "n_4",
                    title = "Profile Updated",
                    message = "Your Fix Finder account settings were updated successfully.",
                    timestamp = "2 days ago",
                    isRead = true,
                    type = NotificationItem.TYPE_INFO
                )
            )
        )
    }
}
