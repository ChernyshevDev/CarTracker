package dev.chernyshev.cartracker.presentation.main_page

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.chernyshev.cartracker.BaseViewModel
import dev.chernyshev.cartracker.R
import dev.chernyshev.cartracker.databinding.FragmentMainPageBinding
import dev.chernyshev.cartracker.presentation.BaseFragment
import dev.chernyshev.cartracker.presentation.adapters.UserInfoAdapter
import dev.chernyshev.cartracker.utils.extensions.checkIfPermissionGranted
import dev.chernyshev.cartracker.utils.extensions.displayHeightPixels
import dev.chernyshev.cartracker.utils.extensions.getLastKnownLocation
import dev.chernyshev.cartracker.utils.extensions.setCurrentPositionMarker
import javax.inject.Inject

class MainPageFragment :
    BaseFragment<MainPageViewModel, FragmentMainPageBinding>() {

    @Inject
    lateinit var userInfoAdapter: UserInfoAdapter

    private var map: GoogleMap? = null
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainPageViewModel::class.java]
        return initBinding(FragmentMainPageBinding.inflate(inflater, container, false))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState(viewModel as BaseViewModel<MainPageViewState>) {
            println("received users: ")
            it.users.forEach {
                println("$it")
            }
            userInfoAdapter.updateItems(it.users.filter { userInfo -> userInfo.owner != null })
        }

        setupUsersRecycler()
        setupBottomSheet()
        initMap()
        viewModel?.fetchUsers()
    }

    private fun setupUsersRecycler() {
        requireBinding {
            mainPageBottomSheetRecycler.adapter = userInfoAdapter.apply {
                setOnVehicleIconClickListener {
                    // TODO
                }
            }
        }
    }

    private fun setupBottomSheet() {
        requireBinding {
            mainPageBottomSheet.apply {
                isClickable = true
                bottomSheetBehaviour = BottomSheetBehavior.from(this)
            }
            bottomSheetBehaviour.apply {
                isDraggable = true
                isHideable = true
                halfExpandedRatio = resources.getDimension(R.dimen.margin_3) / displayHeightPixels
                isFitToContents = false
                expandedOffset = resources.getDimension(R.dimen.margin_1).toInt()

                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        mainPageBottomSheetRecycler.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN

                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mainPageMap) as SupportMapFragment?
        val onMapReady = OnMapReadyCallback { googleMap ->
            map = googleMap
            setupMap()
            checkLocationPermission()
        }
        mapFragment?.getMapAsync(onMapReady)
    }

    private fun setupMap() {
        map?.uiSettings?.isMapToolbarEnabled = false
        setupVehicleMarkers()
    }

    private fun setupVehicleMarkers() {
        // TODO
    }

    private fun checkLocationPermission() {
        val onPermissionGranted = {
            requireContext().getLastKnownLocation(
                onLocationReady = {
                    map?.setCurrentPositionMarker(requireContext(), it, 6f)
                },
                onError = {
                    // todo
                }
            )
        }
        requireContext().checkIfPermissionGranted(
            Manifest.permission.ACCESS_FINE_LOCATION,
            onAlreadyGranted = {
                onPermissionGranted()
            },
            onNoPermission = {
                mainActivity.requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    onPermissionGranted
                )
            })
    }
}
