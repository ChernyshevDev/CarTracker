package dev.chernyshev.cartracker.presentation.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.chernyshev.cartracker.R
import dev.chernyshev.cartracker.databinding.ItemVehicleDescriptionCellBinding
import dev.chernyshev.cartracker.domain.entity.Vehicle

class VehicleInfoCellAdapter :
    RecyclerView.Adapter<VehicleInfoCellAdapter.ItemViewHolder>() {
    private var items: MutableList<Pair<String, String>> = mutableListOf()

    fun updateItems(newItems: List<Pair<String, String>>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleInfoCellAdapter.ItemViewHolder {
        return ItemViewHolder(
            ItemVehicleDescriptionCellBinding.inflate(
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

    inner class ItemViewHolder(private val binding: ItemVehicleDescriptionCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: Pair<String, String>) {
            with(binding) {
                vehicleDescriptionDescription.apply {
                    isVisible = info.second != context.getString(R.string.vehicle_color)
                    text = info.first
                }
                vehicleDescriptionTitle.text = info.second
                vehicleDescriptionColor.apply {
                    isVisible = info.second == context.getString(R.string.vehicle_color)
                    if (isVisible) {
                        setBackgroundColor(Color.parseColor(info.first))
                    }
                }
            }
        }
    }
}

fun Vehicle.toDescriptionPairs(context: Context): List<Pair<String, String>> {
    return listOf(
        Pair(this.color, context.getString(R.string.vehicle_color)),
        Pair(this.make, context.getString(R.string.vehicle_brand)),
        Pair(this.model, context.getString(R.string.vehicle_model)),
        Pair(this.year, context.getString(R.string.vehicle_year)),
        Pair(this.vin, context.getString(R.string.vehicle_vin))
    )
}