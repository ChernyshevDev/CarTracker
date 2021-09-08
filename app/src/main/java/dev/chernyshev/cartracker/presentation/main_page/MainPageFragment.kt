package dev.chernyshev.cartracker.presentation.main_page

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.chernyshev.cartracker.BaseViewModel
import dev.chernyshev.cartracker.R
import dev.chernyshev.cartracker.databinding.FragmentMainPageBinding
import dev.chernyshev.cartracker.domain.contract.LocationProvider
import dev.chernyshev.cartracker.domain.entity.Vehicle
import dev.chernyshev.cartracker.presentation.BaseFragment
import dev.chernyshev.cartracker.presentation.adapters.UserInfoAdapter
import dev.chernyshev.cartracker.utils.extensions.placeVehicleMarker
import dev.chernyshev.cartracker.utils.extensions.moveCameraSmoothly
import dev.chernyshev.cartracker.utils.extensions.displayHeightPixels
import dev.chernyshev.cartracker.utils.extensions.getLastKnownLocation
import dev.chernyshev.cartracker.utils.extensions.setCurrentPositionMarker
import dev.chernyshev.cartracker.utils.extensions.checkIfPermissionGranted
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainPageFragment :
    BaseFragment<MainPageViewModel, FragmentMainPageBinding>(), GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var userInfoAdapter: UserInfoAdapter

    @Inject
    lateinit var locationProvider: LocationProvider

    private var map: GoogleMap? = null
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<View>
    private val vehicleMarkers: HashMap<Int, Marker> = hashMapOf()

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

        observeState(viewModel as BaseViewModel<MainPageViewState>) { state ->
            state.getValidUsers().run {
                userInfoAdapter.updateItems(this)
                forEach { userInfo ->
                    userInfo.userid?.let { userId ->
                        if (state.userVehicleLocations[userId] == null) {
                            viewModel?.fetchUserVehiclesLocations(userId)
                            return@forEach
                        }
                    }
                }
            }

            state.userVehicleLocations.forEach {
                it.value.forEach { info ->
                    var vehicle: Vehicle? = null
                    state.users.values.forEach { userInfo ->
                        userInfo.vehicles.forEach { _vehicle ->
                            if (_vehicle.vehicleid == info.vehicleid) {
                                vehicle = _vehicle
                            }
                        }
                    }
                    vehicle?.let { _vehicle ->
                        map?.placeVehicleMarker(
                            LatLng(info.lat, info.lon),
                            locationProvider.getAddress(
                                LatLng(info.lat, info.lon)
                            ),
                            _vehicle.vehicleid
                        )?.let { marker ->
                            vehicleMarkers[_vehicle.vehicleid] = marker
                        }
                    }
                }
            }
        }

        setupUsersRecycler()
        setupBottomSheet()
        initMap()
        viewModel?.fetchUsers()
    }

    private fun showVehicleInfo(vehicle: Vehicle, position: LatLng?, zoom: Float?) {
        requireBinding {
            updateVehicleInfoVisibility(isVisible = true)
            mainPageBottomSheetVehicleInfo.setData(vehicle)
            mainPageBottomSheetVehicleInfo.viewTreeObserver.addOnGlobalLayoutListener(object :
                OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val vehicleInfoHeight = mainPageBottomSheetVehicleInfo.measuredHeight
                    bottomSheetBehaviour.halfExpandedRatio =
                        (vehicleInfoHeight + resources.getDimension(R.dimen.margin_535)) / displayHeightPixels.toFloat()
                    mainPageBottomSheetVehicleInfo.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    mainPageBottomSheet.requestLayout()
                }
            })
            MainScope().launch {
                map?.moveCameraSmoothly(
                    position,
                    zoom,
                    markerInTop = true,
                    displayHeightPixels
                )
            }
        }
    }

    private fun updateVehicleInfoVisibility(isVisible: Boolean) {
        requireBinding {
            mainPageBottomSheetRecycler.isVisible = !isVisible
            mainPageBottomSheetDivider.isVisible = !isVisible
            mainPageBottomSheetVehicleInfo.isVisible = isVisible
        }
    }

    private fun setupUsersRecycler() {
        requireBinding {
            mainPageBottomSheetRecycler.adapter = userInfoAdapter.apply {
                setOnVehicleIconClickListener {
                    showVehicleInfo(it, vehicleMarkers[it.vehicleid]?.position, 12f)
                    vehicleMarkers[it.vehicleid]?.showInfoWindow()
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
                state = BottomSheetBehavior.STATE_EXPANDED

                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            hideMarkerInfoWindows()
                            if (mainPageBottomSheetVehicleInfo.isVisible) {
                                updateVehicleInfoVisibility(isVisible = false)
                            }
                            bottomSheetBehaviour.halfExpandedRatio =
                                resources.getDimension(R.dimen.margin_3) / displayHeightPixels
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun hideBottomSheet() {
        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_HALF_EXPANDED
            && bottomSheetBehaviour.halfExpandedRatio != resources.getDimension(R.dimen.margin_3) / displayHeightPixels) {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
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
        map?.apply {
            uiSettings?.isMapToolbarEnabled = false
            setOnMarkerClickListener(this@MainPageFragment)
            setOnMapClickListener {
                hideBottomSheet()
                hideMarkerInfoWindows()
            }
        }
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

    override fun onMarkerClick(marker: Marker?): Boolean {
        findVehicle(marker?.tag)?.let {
            showVehicleInfo(it, marker?.position, null)
            vehicleMarkers[it.vehicleid]?.showInfoWindow()
        }
        return false
    }

    private fun findVehicle(markerTag: Any?): Vehicle? {
        var vehicleInfo: Vehicle? = null
        viewModel?.viewData?.value?.users?.values?.forEach {
            it.vehicles.find {
                it.vehicleid == markerTag
            }?.let {
                vehicleInfo = it
                return@forEach
            }
        }
        return vehicleInfo
    }

    private fun hideMarkerInfoWindows() {
        vehicleMarkers?.forEach {
            if (it.value.isInfoWindowShown) {
                it.value.hideInfoWindow()
            }
        }
    }

    private fun MainPageViewState.getValidUsers() = users.values.filter { userInfo -> userInfo.owner != null }
}
