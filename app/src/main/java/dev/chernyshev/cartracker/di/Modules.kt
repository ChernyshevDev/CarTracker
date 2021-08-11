package dev.chernyshev.cartracker.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.chernyshev.cartracker.presentation.MainActivity
import kotlin.reflect.KClass

@Module
abstract class FragmentModule {
//    @ContributesAndroidInjector
//    internal abstract fun contributesSomeFragment(): SomeFragment
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
//    @Provides
//    fun providesSomeProvider(
//        someProviderImpl: SomeProviderImpl
//    ): SomeProvider = someProviderImpl
}

@Module
abstract class ViewModelModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(SearchPageViewModel::class)
//    internal abstract fun bindsSearchPageViewModel(searchPageViewModel: SearchPageViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)