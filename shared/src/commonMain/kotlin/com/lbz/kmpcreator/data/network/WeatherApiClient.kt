package com.lbz.kmpcreator.data.network

import com.lbz.kmpcreator.data.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * 高德天气 API 客户端
 */
object WeatherApiClient {

    private const val BASE_URL = "https://restapi.amap.com/"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 15_000
        }
        defaultRequest {
            url(BASE_URL)
        }
    }

    /**
     * 查询预报天气（extensions=all）
     */
    suspend fun getForecastWeather(key: String, city: String): WeatherResponse =
        httpClient.get("v3/weather/weatherInfo") {
            parameter("key", key)
            parameter("city", city)
            parameter("extensions", "all")
            parameter("output", "JSON")
        }.body()

    /**
     * 查询实况天气（extensions=base）
     */
    suspend fun getLiveWeather(key: String, city: String): WeatherResponse =
        httpClient.get("v3/weather/weatherInfo") {
            parameter("key", key)
            parameter("city", city)
            parameter("extensions", "base")
            parameter("output", "JSON")
        }.body()
}
