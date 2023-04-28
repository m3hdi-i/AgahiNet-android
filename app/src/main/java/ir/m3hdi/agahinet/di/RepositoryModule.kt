package ir.m3hdi.agahinet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.data.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideUserAuthRepository(api: ANetService, dispatcher: CoroutineDispatcher): UserAuthRepository = UserAuthRepository(api,dispatcher)

    @Provides
    @ViewModelScoped
    fun provideAdsRepository(api: ANetService, dispatcher: CoroutineDispatcher): AdRepository = AdRepository(api,dispatcher)

}