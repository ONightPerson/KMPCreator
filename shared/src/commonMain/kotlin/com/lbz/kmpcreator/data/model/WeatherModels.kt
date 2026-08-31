package com.lbz.kmpcreator.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 高德天气 API 响应根对象
 */
@Serializable
data class WeatherResponse(
    @SerialName("status") val status: String,
    @SerialName("count") val count: String? = null,
    @SerialName("info") val info: String? = null,
    @SerialName("infocode") val infocode: String? = null,
    @SerialName("lives") val lives: List<Live>? = null,
    @SerialName("forecasts") val forecasts: List<Forecast>? = null
)

/**
 * 实况天气
 */
@Serializable
data class Live(
    @SerialName("province") val province: String,
    @SerialName("city") val city: String,
    @SerialName("adcode") val adcode: String,
    @SerialName("weather") val weather: String,
    @SerialName("temperature") val temperature: String,
    @SerialName("winddirection") val winddirection: String,
    @SerialName("windpower") val windpower: String,
    @SerialName("humidity") val humidity: String,
    @SerialName("reporttime") val reporttime: String
)

/**
 * 预报天气
 */
@Serializable
data class Forecast(
    @SerialName("city") val city: String,
    @SerialName("adcode") val adcode: String,
    @SerialName("province") val province: String,
    @SerialName("reporttime") val reporttime: String,
    @SerialName("casts") val casts: List<Cast>
)

/**
 * 单日预报
 */
@Serializable
data class Cast(
    @SerialName("date") val date: String,
    @SerialName("week") val week: String,
    @SerialName("dayweather") val dayweather: String,
    @SerialName("nightweather") val nightweather: String,
    @SerialName("daytemp") val daytemp: String,
    @SerialName("nighttemp") val nighttemp: String,
    @SerialName("daywind") val daywind: String,
    @SerialName("nightwind") val nightwind: String,
    @SerialName("daypower") val daypower: String,
    @SerialName("nightpower") val nightpower: String
)

/**
 * 城市数据（城市名 + adcode）
 */
data class City(
    val name: String,
    val adcode: String
)
