package dev.chernyshev.cartracker.presentation.main_page

import dev.chernyshev.cartracker.BaseViewModel
import dev.chernyshev.cartracker.domain.contract.ApiProvider
import dev.chernyshev.cartracker.domain.entity.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainPageViewModel @Inject constructor(
    private val apiProvider: ApiProvider
) : BaseViewModel<MainPageViewState>() {

    fun fetchUsers() {
        MainScope().launch(job) {
            withContext(Dispatchers.IO) {
                val users = apiProvider.getUsers()
                viewData.postValue(MainPageViewState(users))
            }
        }
    }
}

data class MainPageViewState(
    val users: List<UserInfo>
)