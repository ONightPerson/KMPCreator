package com.lbz.kmpcreator.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class LocationProvider {

    companion object {
        private const val TAG = "LocationRepository"

        @Volatile
        private var appContext: Context? = null

        /**
         * 由 MainActivity 在 onCreate 中调用，注入应用上下文
         */
        fun init(context: Context) {
            appContext = context.applicationContext
        }
    }

    private val locationManager: LocationManager? by lazy {
        appContext?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    @SuppressLint("MissingPermission")
    actual fun getLocationUpdates(): Flow<GeoPoint> = callbackFlow {
        val manager = locationManager ?: run {
            Log.w(TAG, "LocationProvider 未初始化，请先调用 LocationProvider.init(context)")
            close()
            return@callbackFlow
        }

        try {
            // 1. 尝试获取最后一次已知位置
            val lastGpsLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastNetworkLocation =
                manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val bestLocation = when {
                lastGpsLocation != null && lastNetworkLocation != null -> {
                    if (lastGpsLocation.time > lastNetworkLocation.time) lastGpsLocation
                    else lastNetworkLocation
                }

                else -> lastGpsLocation ?: lastNetworkLocation
            }
            bestLocation?.let {
                trySend(GeoPoint(it.latitude, it.longitude))
            }

            // 2. 注册实时更新监听
            val listener = LocationListener { location ->
                Log.i(TAG, "getLocationUpdates: location: $location")
                trySend(GeoPoint(location.latitude, location.longitude))
            }

            val providers = manager.getProviders(true)
            Log.i(TAG, "location providers: $providers")

            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                Log.i(TAG, "getLocationUpdates: gps")
                manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                Log.i(TAG, "getLocationUpdates: network")
                manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    3000L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            }

            awaitClose {
                manager.removeUpdates(listener)
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "定位权限未授予: ${e.message}")
            close()
        }
    }
}
