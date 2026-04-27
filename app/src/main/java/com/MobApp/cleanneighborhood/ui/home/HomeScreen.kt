package com.MobApp.cleanneighborhood.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.MobApp.cleanneighborhood.R

private val GreenColor = Color(0xFF609432)
private val GreenDark = Color(0xFF4A7326)
private val GreenLight = Color(0xFFE8F5E9)

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onNavigateToMap: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ─── Hero блок ────────────────────────────────────────
        item {
            HeroBlock(
                onNavigateToMap = onNavigateToMap,
                onNavigateToCatalog = onNavigateToCatalog
            )
        }

        // ─── О нас ────────────────────────────────────────────
        item {
            AboutSection(
                onNavigateToMap = onNavigateToMap,
                onNavigateToCatalog = onNavigateToCatalog
            )
        }

        // ─── Новости заголовок ────────────────────────────────
        item {
            Text(
                text = "НОВОСТИ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 20.dp)
            )
        }

        // ─── Новости список ───────────────────────────────────
        items(uiState.news) { item ->
            NewsCard(
                item = item,
                onToggle = { viewModel.toggleNewsExpanded(item.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ─── Hero блок ────────────────────────────────────────────────

@Composable
fun HeroBlock(
    onNavigateToMap: () -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Фоновое изображение города
        Image(
            painter = painterResource(id = R.drawable.bg_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Затемняющий градиент
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // Контент поверх фото
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "ЧИСТЫЙ КВАРТАЛ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Подзаголовок
            Text(
                text = "Твой вклад в будущее города",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопки
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Что сдать? → Каталог
                Button(
                    onClick = onNavigateToCatalog,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text(
                        text = "Что сдать?",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Где сдать? → Карта
                Button(
                    onClick = onNavigateToMap,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenColor
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text(
                        text = "Где сдать?",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ─── О нас ────────────────────────────────────────────────────

@Composable
fun AboutSection(
    onNavigateToMap: () -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "О НАС",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Карточка 1 — Наша цель
        AboutCard(
            iconRes = R.drawable.ic_about_goal,
            title = "Наша цель",
            description = "Мы стремимся сделать процесс сдачи вторсырья максимально простым, чтобы забота об экологии стала удобной привычкой, а не сложной задачей.",
            tags = listOf("польза", "удобство", "экология"),
            onTagClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Карточка 2 — Что умеет платформа
        AboutCard(
            iconRes = R.drawable.ic_about_platform,
            title = "Что умеет платформа",
            description = "Поиск пунктов приёма вторсырья на интерактивной карте, актуальные цены, правила подготовки отходов, отзывы и рейтинги пунктов, личный кабинет пользователя и организации.",
            tags = listOf("карта", "каталог", "отзывы"),
            onTagClick = { tag ->
                when (tag) {
                    "карта" -> onNavigateToMap()
                    "каталог" -> onNavigateToCatalog()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Карточка 3 — Для кого
        AboutCard(
            iconRes = R.drawable.ic_about_users,
            title = "Для кого",
            description = "Для всех жителей Екатеринбурга, которые хотят сортировать отходы правильно, а также для организаций по приёму вторсырья, которые ищут новых клиентов.",
            tags = listOf("граждане", "организации"),
            onTagClick = { /* TODO */ }
        )
    }
}

@Composable
fun AboutCard(
    iconRes: Int,
    title: String,
    description: String,
    tags: List<String>,
    onTagClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Иконка
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = GreenColor,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Заголовок
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Описание
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Теги
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(GreenLight)
                            .border(1.dp, GreenColor, RoundedCornerShape(50.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTagClick(tag) }
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            color = GreenColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ─── Новостная карточка ───────────────────────────────────────

@Composable
fun NewsCard(
    item: NewsItem,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize(animationSpec = tween(300)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Дата + тег
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                // Тег
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(GreenColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.tag,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Заголовок новости
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Краткий текст всегда виден
            Text(
                text = item.shortText,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            // Полный текст — показывается при раскрытии
            if (item.isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.fullText,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Кнопка раскрытия
            Text(
                text = if (item.isExpanded) "Свернуть" else "Читать полностью",
                fontSize = 13.sp,
                color = GreenColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle
                )
            )
        }
    }
}