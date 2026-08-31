package com.lbz.kmpcreator.data.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * 经纬度坐标（纬度, 经度）
 */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)

/**
 * 平台定位能力
 * - Android: LocationManager 实现
 * - iOS: CoreLocation 实现
 * - 其他平台: 空实现
 */
expect class LocationProvider() {

    /**
     * 位置更新流：先尝试最后一次已知位置，再订阅实时更新
     */
    fun getLocationUpdates(): Flow<GeoPoint>
}

/**
 * 请求定位权限（仅在需要时触发系统弹窗）
 * - Android: 运行时权限申请（需 Activity 已注册权限委托）
 * - iOS: CLLocationManager.requestWhenInUseAuthorization
 * - 其他平台: 无操作
 */
expect fun requestLocationPermission()

/**
 * 定位事件通道：平台侧（如 Android 权限授予后）通知共享层重新获取位置
 */
object LocationEvents {

    private val _refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshRequests: SharedFlow<Unit> = _refreshRequests

    fun requestRefresh() {
        _refreshRequests.tryEmit(Unit)
    }
}
