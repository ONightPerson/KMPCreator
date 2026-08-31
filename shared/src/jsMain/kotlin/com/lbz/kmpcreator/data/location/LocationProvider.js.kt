package com.lbz.kmpcreator.data.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class LocationProvider {
    actual fun getLocationUpdates(): Flow<GeoPoint> = emptyFlow()
}

actual fun requestLocationPermission() {
    // Web 端暂不实现定位
}
