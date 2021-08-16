package dev.chernyshev.cartracker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

open class BaseViewModel<T> : ViewModel() {
    val job = Job()
    val viewData: MutableLiveData<T> = MutableLiveData()

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}