package com.lbz.kmpcreator.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lbz.kmpcreator.data.location.GeoPoint
import com.lbz.kmpcreator.data.location.LocationEvents
import com.lbz.kmpcreator.data.location.LocationProvider
import com.lbz.kmpcreator.data.model.Cast
import com.lbz.kmpcreator.data.model.City
import com.lbz.kmpcreator.data.model.Live
import com.lbz.kmpcreator.data.repository.CityData
import com.lbz.kmpcreator.data.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 天气页面 UI 状态
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val currentCity: City = CityData.defaultCity,
    val liveWeather: Live? = null,
    val forecastList: List<Cast> = emptyList(),
    val reportTime: String = "",
    val location: GeoPoint? = null,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository(),
    private val locationProvider: LocationProvider = LocationProvider()
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherUiState())

    // 用于触发重新获取位置的流（初始化 + 平台侧事件）
    private val locationTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    init {
        // 平台侧事件（如 Android 权限授予）转发为位置触发
        viewModelScope.launch {
            LocationEvents.refreshRequests.collect { locationTrigger.tryEmit(Unit) }
        }
    }

    // 使用 callbackFlow 并转为热流 (StateFlow)，支持通过 trigger 重新订阅
    private val locationFlow = locationTrigger
        .flatMapLatest {
            locationProvider.getLocationUpdates()
                .catch { e -> println("Location error caught: ${e.message}") }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 合并天气状态和经纬度状态
    val uiState: StateFlow<WeatherUiState> = combine(_weatherState, locationFlow) { state, location ->
        state.copy(location = location)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeatherUiState()
    )

    init {
        loadWeather(CityData.defaultCity)
    }

    /**
     * 切换城市并加载天气
     */
    fun selectCity(city: City) {
        if (city.adcode == _weatherState.value.currentCity.adcode) return
        loadWeather(city)
    }

    /**
     * 刷新当前城市天气
     */
    fun refresh() {
        loadWeather(_weatherState.value.currentCity)
        updateLocation()
    }

    /**
     * 重新触发位置更新（通常在权限授予后调用）
     */
    fun updateLocation() {
        locationTrigger.tryEmit(Unit)
    }

    private fun loadWeather(city: City) {
        _weatherState.value = _weatherState.value.copy(
            isLoading = true,
            currentCity = city,
            error = null
        )
        viewModelScope.launch {
            // 并行请求实况与预报
            val liveDeferred = async { repository.getLiveWeather(city.adcode) }
            val forecastDeferred = async { repository.getForecastWeather(city.adcode) }

            val liveResult = liveDeferred.await()
            val forecastResult = forecastDeferred.await()

            var live: Live? = null
            var casts: List<Cast> = emptyList()
            var reportTime = ""
            var errorMsg: String? = null

            liveResult.onSuccess { live = it }
                .onFailure { errorMsg = it.message }

            forecastResult.onSuccess { forecast ->
                casts = forecast.casts
                reportTime = forecast.reporttime
            }.onFailure { errorMsg = it.message }

            _weatherState.value = _weatherState.value.copy(
                isLoading = false,
                liveWeather = live,
                forecastList = casts,
                reportTime = reportTime,
                error = if (live == null && casts.isEmpty()) errorMsg else null
            )
        }
    }
}
