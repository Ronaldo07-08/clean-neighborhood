package com.MobApp.cleanneighborhood

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.yandex.mapkit.MapKitFactory

@HiltAndroidApp
class CleanNeighborhoodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //API ключ Яндекс карт
        MapKitFactory.setApiKey("0f10eb25-307c-4cf6-a815-cfbaaea3f808")
    }
}