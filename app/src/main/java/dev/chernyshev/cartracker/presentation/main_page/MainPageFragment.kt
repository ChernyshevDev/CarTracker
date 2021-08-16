package dev.chernyshev.cartracker.presentation.main_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import dev.chernyshev.cartracker.BaseViewModel
import dev.chernyshev.cartracker.databinding.FragmentMainPageBinding
import dev.chernyshev.cartracker.presentation.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainPageFragment :
    BaseFragment<MainPageViewModel, FragmentMainPageBinding>() {

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

        requireBinding {
            mainPageTextView.text = "Main Screen"
        }

        observeState(viewModel as BaseViewModel<MainPageViewState>) {
            println("received users: ")
            it.users.forEach {
                println("$it")
            }
        }

        viewModel?.fetchUsers()
    }
}
