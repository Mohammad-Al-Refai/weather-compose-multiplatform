package mo.cmp.weather

import android.app.Application
import mo.cmp.weather.di.initAndroidKoin

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initAndroidKoin(this@MainApp)
    }
}