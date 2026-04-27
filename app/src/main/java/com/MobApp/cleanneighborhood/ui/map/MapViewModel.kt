package com.MobApp.cleanneighborhood.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.MobApp.cleanneighborhood.data.UserLocationManager
import com.MobApp.cleanneighborhood.data.model.CollectionPoint
import com.MobApp.cleanneighborhood.data.model.MapFilters
import com.MobApp.cleanneighborhood.data.model.SortOrder
import com.MobApp.cleanneighborhood.data.model.WasteType
import com.MobApp.cleanneighborhood.data.model.WastePrice
import com.MobApp.cleanneighborhood.data.model.PointReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val points: List<CollectionPoint> = emptyList(),
    val filteredPoints: List<CollectionPoint> = emptyList(),
    val filters: MapFilters = MapFilters(),
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val isListMode: Boolean = false,
    val searchQuery: String = "",
    val selectedPoint: CollectionPoint? = null,
    val isFilterSheetOpen: Boolean = false,
    val userLocation: Pair<Double, Double>? = null,
    val detailPoint: CollectionPoint? = null,
    val locationUpdateCount: Int = 0  // счётчик нажатий кнопки
)

@HiltViewModel
class MapViewModel @Inject constructor(private val userLocationManager: UserLocationManager) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Mock данные — заменить на API когда бэкенд будет готов
    private val mockPoints = listOf(
        CollectionPoint(
            id = "1",
            name = "ЭкоПункт",
            address = "г. Екатеринбург, ул. Куйбышева, 21",
            latitude = 56.838011,
            longitude = 60.597474,
            rating = 4.8,
            reviewCount = 12,
            schedule = "Пн-Пт: 9:00-18:00",
            isOpen = true,
            wasteTypes = listOf("Пластик", "Стекло", "Металл"),
            phone = "+7 (495) 987-65-43",
            prices = listOf(
                WastePrice("Пластик", 20),
                WastePrice("Бумага", 10),
                WastePrice("Текстиль", 15)
            ),
            reviews = listOf(
                PointReview(
                    id = "1",
                    username = "ivan229",
                    date = "15.04.2026",
                    rating = 5,
                    text = "Отличный пункт! Быстро принимают, персонал вежливый. Цены адекватные."
                ),
                PointReview(
                    id = "2",
                    username = "sonixks",
                    date = "02.04.2026",
                    rating = 4,
                    text = "Удобное расположение. Принимают без очередей в обеденное время."
                )
            )
        ),
        CollectionPoint(
            id = "2",
            name = "ЭкоЛогия",
            address = "г. Екатеринбург, ул. Коминтерна, 11",
            latitude = 56.842,
            longitude = 60.612,
            rating = 4.5,
            reviewCount = 8,
            schedule = "Ежедневно: 10:00-20:00",
            isOpen = true,
            wasteTypes = listOf("Бумага и картон", "Электроника", "Батарейки"),
            phone = "+7 (343) 123-45-67",
            prices = listOf(
                WastePrice("Бумага и картон", 8),
                WastePrice("Электроника", 50),
                WastePrice("Батарейки", 0)
            ),
            reviews = listOf(
                PointReview(
                    id = "3",
                    username = "ecouser",
                    date = "10.04.2026",
                    rating = 5,
                    text = "Очень удобно, принимают всё что нужно!"
                )
            )
        )
    )

    init {
        _uiState.update { it.copy(
            points = mockPoints,
            filteredPoints = mockPoints
        )}
    }

    // Переключение Карта/Список
    fun toggleListMode() {
        _uiState.update { it.copy(isListMode = !it.isListMode) }
    }

    // Поиск
    fun onSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    // Выбор точки на карте
    fun onPointSelected(point: CollectionPoint?) {
        _uiState.update { it.copy(selectedPoint = point) }
    }

    // Открыть/закрыть фильтры
    fun toggleFilterSheet() {
        _uiState.update { it.copy(isFilterSheetOpen = !it.isFilterSheetOpen) }
    }

    // Применить фильтры
    fun applyFilters(filters: MapFilters = _uiState.value.filters) {
        _uiState.update { it.copy(filters = filters) }
        var result = _uiState.value.points

        // Фильтр по поиску
        if (_uiState.value.searchQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                        it.address.contains(_uiState.value.searchQuery, ignoreCase = true)
            }
        }

        // Фильтр по типу отходов
        if (filters.wasteTypes.isNotEmpty()) {
            result = result.filter { point ->
                filters.wasteTypes.any { type ->
                    point.wasteTypes.contains(type.label)
                }
            }
        }

        // Фильтр по рейтингу
        if (filters.minRating > 0) {
            result = result.filter { it.rating >= filters.minRating }
        }

        // Сортировка
        result = when (_uiState.value.sortOrder) {
            SortOrder.NAME_ASC -> result.sortedBy { it.name }
            SortOrder.NAME_DESC -> result.sortedByDescending { it.name }
            SortOrder.RATING -> result.sortedByDescending { it.rating }
            else -> result
        }

        _uiState.update { it.copy(filteredPoints = result) }
    }

    // Сбросить фильтры
    fun resetFilters() {
        _uiState.update { it.copy(
            filters = MapFilters(),
            filteredPoints = _uiState.value.points
        )}
    }

    // Сортировка
    fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        applyFilters()
    }

    // Обновить фильтры без применения (пока пользователь выбирает)
    fun updateFilters(filters: MapFilters) {
        _uiState.update { it.copy(filters = filters) }
    }

    // Координаты пользователя
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

    // Получить местоположение и центрировать карту
    fun fetchUserLocation() {
        viewModelScope.launch {
            val location = userLocationManager.getCurrentLocation()
            android.util.Log.d("MapViewModel", "Got location: $location")
            if (location != null) {
                _uiState.update {
                    it.copy(
                        userLocation = Pair(location.latitude, location.longitude),
                        locationUpdateCount = it.locationUpdateCount + 1  // увеличиваем счётчик
                    )
                }
            }
        }
    }

    // Открыть детальный просмотр
    fun openPointDetail(point: CollectionPoint) {
        _uiState.update { it.copy(detailPoint = point) }
    }

    // Закрыть детальный просмотр
    fun closePointDetail() {
        _uiState.update { it.copy(detailPoint = null) }
    }
}