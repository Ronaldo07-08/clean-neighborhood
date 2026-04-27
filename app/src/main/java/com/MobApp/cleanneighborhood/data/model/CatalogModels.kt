package com.MobApp.cleanneighborhood.data.model

data class WasteItem(
    val id: String,
    val name: String,
    val category: String,
    val pricePerKg: Int,
    val imageUrl: String? = null
)

data class CatalogFilters(
    val wasteTypes: Set<String> = emptySet(),
    val minPrice: Int? = null,
    val maxPrice: Int? = null
)

enum class CatalogSortOrder(val label: String) {
    DEFAULT("По умолчанию"),
    NAME_ASC("По названию А-Я"),
    NAME_DESC("По названию Я-А"),
    PRICE_ASC("По цене ↑"),
    PRICE_DESC("По цене ↓")
}