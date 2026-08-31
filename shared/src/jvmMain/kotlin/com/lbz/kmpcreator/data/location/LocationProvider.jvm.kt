package com.lbz.kmpcreator.data.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class LocationProvider {
    actual fun getLocationUpdates(): Flow<GeoPoint> = emptyFlow()
}

actual fun requestLocationPermission() {
    // 桌面端无定位权限需求
}
