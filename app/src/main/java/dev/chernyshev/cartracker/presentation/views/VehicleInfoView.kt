package dev.chernyshev.cartracker.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import dev.chernyshev.cartracker.databinding.ViewVehicleInfoBinding
import dev.chernyshev.cartracker.domain.entity.Vehicle
import dev.chernyshev.cartracker.presentation.adapters.VehicleInfoCellAdapter
import dev.chernyshev.cartracker.presentation.adapters.toDescriptionPairs
import dev.chernyshev.cartracker.utils.extensions.loadImage

class VehicleInfoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = ViewVehicleInfoBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(vehicleInfo: Vehicle) {
        with(binding) {
            vehicleInfoImage.loadImage(vehicleInfo.foto)
            vehicleInfoRecycler.adapter = VehicleInfoCellAdapter().apply {
                updateItems(vehicleInfo.toDescriptionPairs(context))
            }
        }
    }
}