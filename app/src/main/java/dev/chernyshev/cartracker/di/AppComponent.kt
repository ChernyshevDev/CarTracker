package dev.chernyshev.cartracker.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.chernyshev.cartracker.CarTrackerApp
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ProvidersModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: CarTrackerApp)
}