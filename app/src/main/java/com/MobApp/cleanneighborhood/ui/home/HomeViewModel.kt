package com.MobApp.cleanneighborhood.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NewsItem(
    val id: String,
    val date: String,
    val tag: String,
    val title: String,
    val shortText: String,
    val fullText: String,
    val isExpanded: Boolean = false
)

data class HomeUiState(
    val news: List<NewsItem> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val mockNews = listOf(
        NewsItem(
            id = "1",
            date = "10 апреля 2026",
            tag = "ПАРТНЁРСТВО",
            title = "4 новых пункта приёма в Екатеринбурге",
            shortText = "Компания \"ВторРесурс\" открыла 4 новых пункта пластика, бумаги и стекла.",
            fullText = "Компания \"ВторРесурс\" открыла 4 новых пункта пластика, бумаги и стекла.\n\n" +
                    "Мы заключили партнёрство с крупнейшей сетью пунктов приёма «ВторРесурс» и добавили на платформу 4 новых точки:\n\n" +
                    "• ул. Ленина, 50 (Ленинский район)\n" +
                    "• пр. Космонавтов, 15 (Орджоникидзевский район)\n" +
                    "• ул. Малышева, 36 (Кировский район)\n" +
                    "• ул. 8 Марта, 123 (Чкаловский район)"
        ),
        NewsItem(
            id = "2",
            date = "5 апреля 2026",
            tag = "ОБНОВЛЕНИЕ",
            title = "Новые функции в приложении",
            shortText = "Добавили фильтрацию по типу отходов и рейтинг пунктов приёма.",
            fullText = "Добавили фильтрацию по типу отходов и рейтинг пунктов приёма.\n\n" +
                    "В новой версии приложения вы можете:\n\n" +
                    "• Фильтровать пункты по типу принимаемых отходов\n" +
                    "• Смотреть рейтинг и отзывы пользователей\n" +
                    "• Искать отходы в каталоге по названию"
        )
    )

    init {
        _uiState.update { it.copy(news = mockNews) }
    }

    fun toggleNewsExpanded(id: String) {
        _uiState.update { state ->
            state.copy(
                news = state.news.map { item ->
                    if (item.id == id) item.copy(isExpanded = !item.isExpanded)
                    else item
                }
            )
        }
    }
}