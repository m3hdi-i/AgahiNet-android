package ir.m3hdi.agahinet

import android.app.Application
import androidx.core.content.res.ResourcesCompat
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.imageLoader
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import es.dmoral.toasty.Toasty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        // Set app's font for toasts
        val typeface = ResourcesCompat.getFont(this, R.font.vazirmatn_regular)
        Toasty.Config.getInstance()
            .setToastTypeface(typeface!!)
            .tintIcon(true)
            .setRTL(true)
            .apply()

        // Config imageLoader singleton of Coil
        val coilImageLoader = ImageLoader.Builder(this)
            /*.memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }*/
            .crossfade(true)
            .build()

        Coil.setImageLoader(coilImageLoader)


    }

}