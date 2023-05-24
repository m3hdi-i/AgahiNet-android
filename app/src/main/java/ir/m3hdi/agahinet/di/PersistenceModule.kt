package ir.m3hdi.agahinet.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.m3hdi.agahinet.data.local.AppDatabase
import ir.m3hdi.agahinet.data.local.dao.CityDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, "AgahiNet.db")
            .createFromAsset("agahinet.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCityDao(appDatabase: AppDatabase): CityDao = appDatabase.cityDao()
}