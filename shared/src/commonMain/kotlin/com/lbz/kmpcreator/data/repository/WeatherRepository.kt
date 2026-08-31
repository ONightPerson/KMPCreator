package com.lbz.kmpcreator.data.repository

import com.lbz.kmpcreator.data.model.Forecast
import com.lbz.kmpcreator.data.model.Live
import com.lbz.kmpcreator.data.network.WeatherApiClient
import kotlinx.coroutines.CancellationException

class WeatherRepository {

    companion object {
        const val API_KEY = "0a0aebfb449b8c337d0920d96532a6ac"
    }

    /**
     * 获取预报天气（今天 + 未来3天）
     */
    suspend fun getForecastWeather(adcode: String): Result<Forecast> =
        try {
            val response = WeatherApiClient.getForecastWeather(key = API_KEY, city = adcode)
            val forecast = response.forecasts?.firstOrNull()
            if (response.status == "1" && forecast != null) {
                Result.success(forecast)
            } else {
                Result.failure(Exception("获取天气失败: ${response.info ?: "未知错误"}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * 获取实况天气
     */
    suspend fun getLiveWeather(adcode: String): Result<Live> =
        try {
            val response = WeatherApiClient.getLiveWeather(key = API_KEY, city = adcode)
            val live = response.lives?.firstOrNull()
            if (response.status == "1" && live != null) {
                Result.success(live)
            } else {
                Result.failure(Exception("获取实况天气失败: ${response.info ?: "未知错误"}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
