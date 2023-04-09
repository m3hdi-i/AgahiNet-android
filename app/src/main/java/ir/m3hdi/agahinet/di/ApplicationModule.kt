package ir.m3hdi.agahinet.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.data.repository.UserAuthRepository
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {


    /*
        Retrofit-related dependencies
    */

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE
        return OkHttpClient
            .Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        //.baseUrl("http://127.0.0.1:8000/")
        .baseUrl("http://10.0.3.2:8000/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideANetService(retrofit: Retrofit): ANetService = retrofit.create(ANetService::class.java)



    /*
        Repository dependencies
    */
    @Provides
    @Singleton
    fun provideUserAuthRepository(api: ANetService,dispatcher: CoroutineDispatcher): UserAuthRepository = UserAuthRepository(api,dispatcher)

/*
    /*
        ROOM Persistence dependencies
    */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "instaget-db").build()

    @Provides
    fun provideMediaDao(appDatabase: AppDatabase): MediaDao = appDatabase.mediaDao()

    /*
        Utils
    */

    @Provides
    @Singleton
    fun providePicasso(): Picasso = Picasso.get()
*/

    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideSecureSharedPreferences(@ApplicationContext context: Context)= AppUtils.getSecureSharedPref(context)

}