package dev.chernyshev.cartracker.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.chernyshev.cartracker.data.api.ApiProviderImpl
import dev.chernyshev.cartracker.domain.contract.ApiProvider
import dev.chernyshev.cartracker.presentation.main_page.MainPageFragment
import dev.chernyshev.cartracker.presentation.MainActivity
import dev.chernyshev.cartracker.presentation.main_page.MainPageViewModel
import kotlin.reflect.KClass

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributesMainPageFragment(): MainPageFragment
}

@Module
abstract class ActivityModule {
    @Binds
    internal abstract fun provideContext(app: Application): Context

    @ContributesAndroidInjector
    internal abstract fun bindsMainActivity(): MainActivity
}

@Module
internal class ProvidersModule {
    @Provides
    fun providesApiProvider(
        apiProviderImpl: ApiProviderImpl
    ): ApiProvider = apiProviderImpl
}

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainPageViewModel::class)
    internal abstract fun bindsMainPageViewModel(mainPageViewModel: MainPageViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)