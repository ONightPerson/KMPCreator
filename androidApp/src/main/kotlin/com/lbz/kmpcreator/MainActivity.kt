package com.lbz.kmpcreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lbz.kmpcreator.data.location.AndroidLocationPermissionDelegate
import com.lbz.kmpcreator.data.location.LocationProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 初始化共享层定位能力与权限申请器（必须在 STARTED 之前）
        LocationProvider.init(this)
        AndroidLocationPermissionDelegate.attach(this)

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        AndroidLocationPermissionDelegate.detach()
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
