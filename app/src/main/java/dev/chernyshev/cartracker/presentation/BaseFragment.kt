package dev.chernyshev.cartracker.presentation

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import dagger.android.support.AndroidSupportInjection
import dev.chernyshev.cartracker.BaseViewModel
import dev.chernyshev.cartracker.R
import javax.inject.Inject

abstract class BaseFragment<VM : ViewModel, B : ViewBinding> : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var viewModel: VM? = null
    private var binding: B? = null

    val mainActivity by lazy {
        requireActivity() as MainActivity
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    fun initBinding(binding: B): View {
        this.binding = binding
        return binding.root
    }

    fun requireBinding(block: (B.() -> Unit)) {
        binding?.apply { block.invoke(this) }
    }

    fun <T> Fragment.observeState(viewModel: BaseViewModel<T>, onUpdate: (T) -> Unit) {
        viewModel.viewData.observe(viewLifecycleOwner) {
            onUpdate(it)
        }
    }

    fun displayError(title: String? = null, message: String? = null) {
        AlertDialog.Builder(this).apply {
            setTitle(title ?: getString(R.string.common_unknown_error_title))
            setMessage(message ?: getString(R.string.common_unknown_error_message))
            setPositiveButton(R.string.common_ok) { _, _ -> }
            show()
        }
    }
}