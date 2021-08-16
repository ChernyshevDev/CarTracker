package dev.chernyshev.cartracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.chernyshev.cartracker.databinding.ItemCarIconBinding
import dev.chernyshev.cartracker.databinding.ItemUserInfoBinding
import dev.chernyshev.cartracker.domain.entity.UserInfo
import dev.chernyshev.cartracker.domain.entity.Vehicle
import dev.chernyshev.cartracker.utils.extensions.loadImage
import javax.inject.Inject

class UserInfoAdapter @Inject constructor() :
    RecyclerView.Adapter<UserInfoAdapter.ItemViewHolder>() {
    private var items: MutableList<UserInfo> = mutableListOf()
    private var onVehicleIconClick: ((Vehicle) -> Unit)? = null
    fun updateItems(newItems: List<UserInfo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setOnVehicleIconClickListener(onClick: (Vehicle) -> Unit) {
        onVehicleIconClick = onClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserInfoAdapter.ItemViewHolder {
        return ItemViewHolder(
            ItemUserInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ItemViewHolder(private val binding: ItemUserInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: UserInfo) {
            with(binding) {
                userInfoPhoto.loadImage(info.owner?.foto)
                userInfoName.text = "${info.owner?.name} ${info.owner?.surname}"
                userInfoCarsRecycler.adapter = CarIconsAdapter().apply {
                    updateItems(info.vehicles, onVehicleIconClick)
                }
            }
        }
    }
}


class CarIconsAdapter : RecyclerView.Adapter<CarIconsAdapter.ItemViewHolder>() {
    private var items: MutableList<Vehicle> = mutableListOf()
    private var onVehicleIconClick: ((Vehicle) -> Unit)? = null
    fun updateItems(newItems: List<Vehicle>, onClick: ((Vehicle) -> Unit)?) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()

        onVehicleIconClick = onClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarIconsAdapter.ItemViewHolder {
        return ItemViewHolder(
            ItemCarIconBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ItemViewHolder(private val binding: ItemCarIconBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vehicle: Vehicle) {
            binding.apply {
                root.setOnClickListener {
                    onVehicleIconClick?.invoke(vehicle)
                }

                carIconImage.loadImage(vehicle.foto)
                carIconTitle.text = vehicle.make
            }

        }
    }
}

