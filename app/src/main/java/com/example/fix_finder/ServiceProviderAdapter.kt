package com.example.fix_finder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fix_finder.data.model.ServiceProvider
import com.google.android.material.button.MaterialButton

class ServiceProviderAdapter(
    private var providers: List<ServiceProvider>,
    private val onViewProfileClick: (ServiceProvider) -> Unit,
    private val onQuickBookClick: (ServiceProvider) -> Unit
) : RecyclerView.Adapter<ServiceProviderAdapter.ProviderViewHolder>() {

    fun updateData(newProviders: List<ServiceProvider>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = providers.size
            override fun getNewListSize(): Int = newProviders.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return providers[oldItemPosition].id == newProviders[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return providers[oldItemPosition] == newProviders[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.providers = newProviders
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service_provider, parent, false)
        return ProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        val provider = providers[position]
        holder.bind(provider, onViewProfileClick, onQuickBookClick)
    }

    override fun getItemCount(): Int = providers.size

    class ProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
        private val tvStartingPrice: TextView = itemView.findViewById(R.id.tvStartingPrice)
        private val btnViewProfile: MaterialButton = itemView.findViewById(R.id.btnViewProfile)
        private val btnQuickBook: MaterialButton = itemView.findViewById(R.id.btnQuickBook)

        fun bind(
            provider: ServiceProvider,
            onViewProfileClick: (ServiceProvider) -> Unit,
            onQuickBookClick: (ServiceProvider) -> Unit
        ) {
            tvName.text = provider.name
            tvCategory.text = "${provider.categoryName} Professional"
            tvRating.text = provider.rating.toString()
            tvDetails.text = "•  ${provider.yearsExperience} yrs exp.  •  ${provider.jobsCompleted} jobs done"
            tvStartingPrice.text = provider.startingPriceDisplay

            val avatarRes = when (provider.avatarName) {
                "avatar_thabo" -> R.drawable.avatar_thabo
                "avatar_sipho" -> R.drawable.avatar_sipho
                "avatar_lerato" -> R.drawable.avatar_lerato
                else -> R.drawable.avatar_thabo
            }
            imgAvatar.setImageResource(avatarRes)

            itemView.setOnClickListener { onViewProfileClick(provider) }
            btnViewProfile.setOnClickListener { onViewProfileClick(provider) }
            btnQuickBook.setOnClickListener { onQuickBookClick(provider) }
        }
    }
}
