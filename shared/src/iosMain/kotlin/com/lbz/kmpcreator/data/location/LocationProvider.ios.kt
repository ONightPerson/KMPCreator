package com.lbz.kmpcreator.data.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.darwin.NSObject

actual class LocationProvider {

    @OptIn(ExperimentalForeignApi::class)
    actual fun getLocationUpdates(): Flow<GeoPoint> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                location.coordinate.useContents {
                    trySend(GeoPoint(latitude, longitude))
                }
            }

            override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
                // 用户同意授权后开始定位更新
                if (didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                    didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways
                ) {
                    manager.startUpdatingLocation()
                }
            }
        }

        // 未决定时请求「使用期间」定位权限（需要 Info.plist 配置用途说明）
        val status = CLLocationManager.authorizationStatus()
        if (status == kCLAuthorizationStatusNotDetermined) {
            manager.requestWhenInUseAuthorization()
        }

        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        manager.distanceFilter = 10.0
        if (status == kCLAuthorizationStatusAuthorizedWhenInUse ||
            status == kCLAuthorizationStatusAuthorizedAlways
        ) {
            manager.startUpdatingLocation()
        }

        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
        }
    }
}

actual fun requestLocationPermission() {
    if (CLLocationManager.authorizationStatus() == kCLAuthorizationStatusNotDetermined) {
        CLLocationManager().requestWhenInUseAuthorization()
    }
}
