package com.example.fix_finder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fix_finder.data.model.ServiceRequest

class BookingsAdapter(
    private var requests: List<ServiceRequest>
) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    fun updateData(newRequests: List<ServiceRequest>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = requests.size
            override fun getNewListSize(): Int = newRequests.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return requests[oldItemPosition].id == newRequests[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return requests[oldItemPosition] == newRequests[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.requests = newRequests
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service_request, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRequestId: TextView = itemView.findViewById(R.id.tvRequestId)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        private val tvServiceName: TextView = itemView.findViewById(R.id.tvServiceName)
        private val tvProviderName: TextView = itemView.findViewById(R.id.tvProviderName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvEstimatedPrice: TextView = itemView.findViewById(R.id.tvEstimatedPrice)

        fun bind(request: ServiceRequest) {
            tvRequestId.text = "#${request.id}"
            tvServiceName.text = request.serviceName
            tvProviderName.text = "Technician: ${request.providerName} (${request.providerCategory})"
            tvDescription.text = request.description
            tvDateTime.text = "📅 ${request.date} at ${request.time}"
            tvAddress.text = "📍 ${request.address}"
            tvEstimatedPrice.text = request.estimatedPrice

            // Status Badge Formatting
            tvStatusBadge.text = request.status
            when (request.status) {
                ServiceRequest.STATUS_ACCEPTED -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DCFCE7"))
                    tvStatusBadge.setTextColor(Color.parseColor("#15803D"))
                }
                ServiceRequest.STATUS_IN_PROGRESS -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DBEAFE"))
                    tvStatusBadge.setTextColor(Color.parseColor("#1D4ED8"))
                }
                ServiceRequest.STATUS_COMPLETED -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0E7FF"))
                    tvStatusBadge.setTextColor(Color.parseColor("#4338CA"))
                }
                ServiceRequest.STATUS_CANCELLED -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEE2E2"))
                    tvStatusBadge.setTextColor(Color.parseColor("#B91C1C"))
                }
                else -> { // PENDING
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF3C7"))
                    tvStatusBadge.setTextColor(Color.parseColor("#B45309"))
                }
            }
        }
    }
}
