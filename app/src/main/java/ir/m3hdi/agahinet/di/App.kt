package ir.m3hdi.agahinet.di

import android.app.Application
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.HiltAndroidApp
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R

@HiltAndroidApp
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        val typeface = ResourcesCompat.getFont(this, R.font.vazirmatn_regular)
        Toasty.Config.getInstance()
            .setToastTypeface(typeface!!)
            .tintIcon(true)
            .setRTL(true)
            .apply()
    }
}