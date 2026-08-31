package com.lbz.kmpcreator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lbz.kmpcreator.ui.weather.WeatherScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        WeatherScreen(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        )
    }
}
