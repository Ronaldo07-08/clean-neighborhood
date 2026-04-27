package com.MobApp.cleanneighborhood.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class UserLocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val gpsEnabled = locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
                val networkEnabled = locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )

                android.util.Log.d("Location", "GPS: $gpsEnabled, Network: $networkEnabled")

                // Сначала пробуем getLastKnownLocation
                val lastKnown = when {
                    gpsEnabled -> locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                    )
                    networkEnabled -> locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER
                    )
                    else -> null
                }

                if (lastKnown != null) {
                    android.util.Log.d("Location", "LastKnown: ${lastKnown.latitude}, ${lastKnown.longitude}")
                    continuation.resume(lastKnown)
                    return@suspendCancellableCoroutine
                }

                // Если lastKnown = null — активно запрашиваем координаты
                android.util.Log.d("Location", "LastKnown is null, requesting updates...")

                val provider = when {
                    gpsEnabled -> LocationManager.GPS_PROVIDER
                    networkEnabled -> LocationManager.NETWORK_PROVIDER
                    else -> {
                        android.util.Log.e("Location", "No provider available")
                        continuation.resume(null)
                        return@suspendCancellableCoroutine
                    }
                }

                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        android.util.Log.d(
                            "Location",
                            "Got location: ${location.latitude}, ${location.longitude}"
                        )
                        // Получили координаты — отписываемся и возвращаем
                        locationManager.removeUpdates(this)
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }

                    override fun onProviderDisabled(provider: String) {
                        android.util.Log.e("Location", "Provider disabled: $provider")
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(
                        provider: String?,
                        status: Int,
                        extras: Bundle?
                    ) {}
                }

                locationManager.requestLocationUpdates(
                    provider,
                    0L,    // минимальное время между обновлениями
                    0f,    // минимальное расстояние
                    listener
                )

                // Если корутина отменена — отписываемся
                continuation.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }

            } catch (e: Exception) {
                android.util.Log.e("Location", "Exception: ${e.message}")
                continuation.resume(null)
            }
        }
    }
}