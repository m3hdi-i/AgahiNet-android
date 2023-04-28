package ir.m3hdi.agahinet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {


/*

    @Provides
    @Singleton
    fun providePicasso(): Picasso = Picasso.get()
*/

    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideSecureSharedPreferences(@ApplicationContext context: Context)= AppUtils.getSecureSharedPref(context)

}