package com.MobApp.cleanneighborhood.data.model

// Пункт приёма
data class CollectionPoint(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val reviewCount: Int,
    val schedule: String,
    val isOpen: Boolean,
    val wasteTypes: List<String>,
    // Новые поля для детального просмотра
    val phone: String = "",
    val prices: List<WastePrice> = emptyList(),
    val reviews: List<PointReview> = emptyList()
)

// Цена на конкретный тип отхода
data class WastePrice(
    val wasteType: String,
    val pricePerKg: Int
)

// Отзыв пользователя
data class PointReview(
    val id: String,
    val username: String,
    val date: String,
    val rating: Int,
    val text: String
)

// Типы отходов
enum class WasteType(val label: String) {
    PLASTIC("Пластик"),
    GLASS("Стекло"),
    ELECTRONICS("Электроника"),
    METAL("Металл"),
    PAPER("Бумага и картон"),
    FURNITURE("Мебель и крупногабарит"),
    TEXTILE("Текстиль"),
    BATTERIES("Батарейки")
}

// Время работы фильтр
enum class WorkingHours {
    OPEN_NOW,
    AROUND_THE_CLOCK,
    OPEN_AT_SPECIFIED_TIME
}

// Сортировка
enum class SortOrder(val label: String) {
    DEFAULT("По умолчанию"),
    NAME_ASC("По названию А-Я"),
    NAME_DESC("По названию Я-А"),
    RATING("По рейтингу"),
    DISTANCE("По расстоянию")
}

// Состояние фильтров
data class MapFilters(
    val wasteTypes: Set<WasteType> = emptySet(),
    val radiusKm: Float = 0f,
    val minRating: Float = 0f,
    val workingHours: WorkingHours? = null
)