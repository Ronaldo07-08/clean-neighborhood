package com.MobApp.cleanneighborhood.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.MobApp.cleanneighborhood.data.model.CatalogFilters
import com.MobApp.cleanneighborhood.data.model.CatalogSortOrder
import com.MobApp.cleanneighborhood.data.model.WasteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CatalogUiState(
    val items: List<WasteItem> = emptyList(),
    val filteredItems: List<WasteItem> = emptyList(),
    val searchQuery: String = "",
    val filters: CatalogFilters = CatalogFilters(),
    val sortOrder: CatalogSortOrder = CatalogSortOrder.DEFAULT,
    val isFilterSheetOpen: Boolean = false
)

@HiltViewModel
class CatalogViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    // Категории отходов
    val wasteCategories = listOf(
        "Пластик",
        "Стекло",
        "Электроника",
        "Металл",
        "Бумага и картон",
        "Мебель и крупногабарит",
        "Текстиль",
        "Батарейки"
    )

    // Mock данные
    private val mockItems = listOf(
        WasteItem(
            id = "1",
            name = "Книга",
            category = "Бумага и картон",
            pricePerKg = 34
        ),
        WasteItem(
            id = "2",
            name = "Пластиковые стаканы",
            category = "Пластик",
            pricePerKg = 5
        ),
        WasteItem(
            id = "3",
            name = "Пластиковая бутылка",
            category = "Пластик",
            pricePerKg = 22
        ),
        WasteItem(
            id = "4",
            name = "Стеклянная бутылка",
            category = "Стекло",
            pricePerKg = 15
        ),
        WasteItem(
            id = "5",
            name = "Алюминиевая банка",
            category = "Металл",
            pricePerKg = 45
        ),
        WasteItem(
            id = "6",
            name = "Газета",
            category = "Бумага и картон",
            pricePerKg = 8
        ),
        WasteItem(
            id = "7",
            name = "Смартфон",
            category = "Электроника",
            pricePerKg = 200
        ),
        WasteItem(
            id = "8",
            name = "Батарейки AA",
            category = "Батарейки",
            pricePerKg = 0
        )
    )

    init {
        _uiState.update {
            it.copy(
                items = mockItems,
                filteredItems = mockItems
            )
        }
    }

    fun onSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun toggleFilterSheet() {
        _uiState.update { it.copy(isFilterSheetOpen = !it.isFilterSheetOpen) }
    }

    fun applyFilters(filters: CatalogFilters = _uiState.value.filters) {
        _uiState.update { it.copy(filters = filters) }
        var result = _uiState.value.items

        // Поиск по названию
        if (_uiState.value.searchQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(_uiState.value.searchQuery, ignoreCase = true)
            }
        }

        // Фильтр по категории
        if (filters.wasteTypes.isNotEmpty()) {
            result = result.filter { it.category in filters.wasteTypes }
        }

        // Фильтр по цене
        filters.minPrice?.let { min ->
            result = result.filter { it.pricePerKg >= min }
        }
        filters.maxPrice?.let { max ->
            result = result.filter { it.pricePerKg <= max }
        }

        // Сортировка
        result = when (_uiState.value.sortOrder) {
            CatalogSortOrder.NAME_ASC -> result.sortedBy { it.name }
            CatalogSortOrder.NAME_DESC -> result.sortedByDescending { it.name }
            CatalogSortOrder.PRICE_ASC -> result.sortedBy { it.pricePerKg }
            CatalogSortOrder.PRICE_DESC -> result.sortedByDescending { it.pricePerKg }
            else -> result
        }

        _uiState.update { it.copy(filteredItems = result) }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                filters = CatalogFilters(),
                filteredItems = it.items,
                searchQuery = ""
            )
        }
    }

    fun setSortOrder(order: CatalogSortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        applyFilters()
    }

    fun clearError() {
        // для будущего использования
    }
}