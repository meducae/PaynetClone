package uz.gita.paynetclone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.paynetclone.presenter.navigation.AppNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    private var appNavigator: AppNavigator? = null

    fun setNavigator(navigator: AppNavigator) {
        appNavigator = navigator
    }

    @Provides
    @Singleton
    fun provideAppNavigator(): AppNavigator {
        return appNavigator ?: throw IllegalStateException("AppNavigator must be initialized in MainActivity")
    }
}
