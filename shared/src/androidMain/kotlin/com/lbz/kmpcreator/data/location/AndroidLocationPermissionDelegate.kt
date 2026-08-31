package com.lbz.kmpcreator.data.location

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

/**
 * Android 定位权限委托：
 * 由 Activity 在 onCreate 中调用 [attach] 注册权限申请器，
 * 共享代码通过 [requestLocationPermission] 触发权限申请。
 */
object AndroidLocationPermissionDelegate {

    private const val TAG = "LocationPermission"

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var activity: ComponentActivity? = null
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    @Volatile
    var lastGranted: Boolean? = null
        private set

    /**
     * 必须在 Activity 的 onCreate 中（STARTED 之前）调用
     */
    fun attach(activity: ComponentActivity) {
        this.activity = activity
        this.launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.values.any { it }
            Log.i(TAG, "定位权限申请结果: $result")
            lastGranted = granted
            if (granted) {
                // 权限授予后通知共享层重新获取位置
                LocationEvents.requestRefresh()
            }
        }
    }

    fun detach() {
        launcher = null
        activity = null
    }

    fun hasAnyPermission(): Boolean {
        val ctx = activity ?: return false
        return permissions.any {
            ctx.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    internal fun request() {
        val ctx = activity ?: run {
            Log.w(TAG, "请先在 MainActivity.onCreate 中调用 AndroidLocationPermissionDelegate.attach(this)")
            return
        }
        if (hasAnyPermission()) {
            Log.i(TAG, "定位权限已授予")
            return
        }
        Log.i(TAG, "发起定位权限申请")
        launcher?.launch(permissions)
    }
}

actual fun requestLocationPermission() {
    AndroidLocationPermissionDelegate.request()
}
