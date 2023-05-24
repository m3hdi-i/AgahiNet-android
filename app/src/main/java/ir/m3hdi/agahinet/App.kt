package ir.m3hdi.agahinet

import android.app.Application
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.HiltAndroidApp
import es.dmoral.toasty.Toasty

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
    }
}