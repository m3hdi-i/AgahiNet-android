package ir.m3hdi.agahinet.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

/*
    /*
        Retrofit-related dependencies
    */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl("https://insta-get.ir/api/v3/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        //.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideIgService(retrofit: Retrofit): IgApiService = retrofit.create(IgApiService::class.java)

    @Provides
    @Singleton
    fun provideDownloadService(): DownloadService{
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.aaa.com")
            .callbackExecutor(Executors.newSingleThreadExecutor())// Obtained through thread pool 1 Threads, specifying callback Run in child threads.
            .build()
        return retrofit.create(DownloadService::class.java)
    }

    @Provides
    @Singleton
    fun provideDownloadUtil(downloadService: DownloadService,@ApplicationContext context: Context): DownloadUtil = DownloadUtil(downloadService,context)


    /*
        Repository dependencies
    */
    @Provides
    @Singleton
    fun provideMediaRepository(api: IgApiService): MediaRepository = MediaRepository(api)


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

}