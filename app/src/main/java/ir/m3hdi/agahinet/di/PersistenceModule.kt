package ir.m3hdi.agahinet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    /*
        ROOM Persistence dependencies
    */
    /*@Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "my-db").build()

    @Provides
    fun provideMediaDao(appDatabase: AppDatabase): MediaDao = appDatabase.mediaDao()*/
}