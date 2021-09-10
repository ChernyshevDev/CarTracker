package dev.chernyshev.cartracker.presentation.main_page

import android.os.CountDownTimer
import dev.chernyshev.cartracker.BaseViewModel
import dev.chernyshev.cartracker.domain.contract.ApiProvider
import dev.chernyshev.cartracker.domain.entity.UserInfo
import dev.chernyshev.cartracker.domain.entity.VehicleLocationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainPageViewModel @Inject constructor(
    private val apiProvider: ApiProvider
) : BaseViewModel<MainPageViewState>() {
    private val updateTimers = hashMapOf<UserId, CountDownTimer>()

    init {
        viewData.value = MainPageViewState(hashMapOf(), hashMapOf())
    }

    fun fetchUsers() {
        MainScope().launch(job) {
            withContext(Dispatchers.IO) {
                val users = apiProvider.getUsers()
                viewData.value?.copy()?.let { newState ->
                    users.forEach {
                        it.userid?.let { id ->
                            newState.users[id] = it
                        }
                    }
                    viewData.postValue(newState)
                }
            }
        }
    }

    fun fetchUserVehiclesLocations(userId: Int) {
        MainScope().launch(job) {
            withContext(Dispatchers.IO) {
                val vehicles = apiProvider.getUserVehiclesLocation(userId)
                val newViewData = viewData.value?.copy()?.apply {
                    userVehicleLocations[userId] = vehicles
                }
                newViewData?.let {
                    viewData.postValue((it))
                }

                withContext(Dispatchers.Main) {
                    startLocationUpdateTimer(userId)
                }
            }
        }
    }

    private fun startLocationUpdateTimer(userId: UserId) {
        if (updateTimers[userId] == null) {
            updateTimers[userId] = object : CountDownTimer(30_000, 30_000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    val newViewData = viewData.value?.copy()?.apply {
                        userVehicleLocations[userId] = emptyList()
                    }
                    newViewData?.let {
                        viewData.postValue((it))
                    }
                    this.cancel()
                    updateTimers.remove(userId)
                }
            }.start()
        }
    }
}

data class MainPageViewState(
    val users: HashMap<UserId, UserInfo>,
    val userVehicleLocations: HashMap<UserId, List<VehicleLocationInfo>>
)

typealias UserId = Int