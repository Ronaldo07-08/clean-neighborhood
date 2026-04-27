package com.MobApp.cleanneighborhood.ui.catalog

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.MobApp.cleanneighborhood.R
import com.MobApp.cleanneighborhood.data.model.CatalogFilters
import com.MobApp.cleanneighborhood.data.model.CatalogSortOrder
import com.MobApp.cleanneighborhood.data.model.WasteItem

private val GreenColor = Color(0xFF609432)
private val GreenLight = Color(0xFFE8F5E9)

@Composable
fun CatalogScreen(
    paddingValues: PaddingValues,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Верхняя часть — белый фон
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Заголовок
                Text(
                    text = "КАТАЛОГ ОТХОДОВ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Строка поиска
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQuery,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 44.dp, end = 16.dp),
                        decorationBox = { innerTextField ->
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "Найти: стакан, коробка...",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .size(20.dp)
                    )
                }
            }

            // Панель — найдено + сортировка + фильтры
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Найдено N отходов
                Row {
                    Text(
                        text = "Найдено: ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${uiState.filteredItems.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenColor
                    )
                    Text(
                        text = " отходов",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка фильтры
                    OutlinedButton(
                        onClick = viewModel::toggleFilterSheet,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GreenColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "Фильтры", fontSize = 12.sp, color = GreenColor)
                    }

                    // Сортировка
                    var sortExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { sortExpanded = true },
                            shape = RoundedCornerShape(50.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = uiState.sortOrder.label,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            CatalogSortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = order.label,
                                            color = if (order == uiState.sortOrder)
                                                GreenColor
                                            else
                                                Color.Black
                                        )
                                    },
                                    onClick = {
                                        viewModel.setSortOrder(order)
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            // Сетка карточек — 2 колонки
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredItems) { item ->
                    WasteItemCard(
                        item = item,
                        onClick = { /* TODO: детальный просмотр отхода */ }
                    )
                }
            }
        }

        // Bottomsheet фильтров
        if (uiState.isFilterSheetOpen) {
            CatalogFilterSheet(
                currentFilters = uiState.filters,
                categories = viewModel.wasteCategories,
                onApply = { filters ->
                    viewModel.applyFilters(filters)
                    viewModel.toggleFilterSheet()
                },
                onReset = {
                    viewModel.resetFilters()
                    viewModel.toggleFilterSheet()
                },
                onDismiss = viewModel::toggleFilterSheet
            )
        }
    }
}

// ─── Карточка отхода ──────────────────────────────────────────

@Composable
fun WasteItemCard(
    item: WasteItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Изображение — серый placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                // Если есть изображение — показываем его
                // иначе — иконка placeholder
                Icon(
                    painter = painterResource(id = R.drawable.ic_image_placeholder),
                    contentDescription = null,
                    tint = GreenColor,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Название
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Категория
            Text(
                text = item.category,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Цена + кнопка перехода
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.pricePerKg > 0)
                        "${item.pricePerKg} руб/кг"
                    else
                        "бесплатно",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenColor
                )

                // Кнопка перехода
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GreenColor)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ─── Bottomsheet фильтров каталога ────────────────────────────

@Composable
fun CatalogFilterSheet(
    currentFilters: CatalogFilters,
    categories: List<String>,
    onApply: (CatalogFilters) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var localFilters by remember { mutableStateOf(currentFilters) }
    var minPriceText by remember {
        mutableStateOf(currentFilters.minPrice?.toString() ?: "")
    }
    var maxPriceText by remember {
        mutableStateOf(currentFilters.maxPrice?.toString() ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Фильтры",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Типы отходов
                    Text(
                        text = "Типы отходов",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = category in localFilters.wasteTypes,
                                onCheckedChange = { checked ->
                                    localFilters = localFilters.copy(
                                        wasteTypes = if (checked)
                                            localFilters.wasteTypes + category
                                        else
                                            localFilters.wasteTypes - category
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GreenColor,
                                    uncheckedColor = Color.Gray
                                )
                            )
                            Text(text = category, fontSize = 14.sp)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // Цена за кг
                    Text(
                        text = "Цена за кг, руб",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Поле "от"
                        OutlinedTextField(
                            value = minPriceText,
                            onValueChange = { value ->
                                minPriceText = value
                                localFilters = localFilters.copy(
                                    minPrice = value.toIntOrNull()
                                )
                            },
                            placeholder = {
                                Text(text = "от", fontSize = 13.sp, color = Color.Gray)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenColor,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Text(text = "—", color = Color.Gray)

                        // Поле "до"
                        OutlinedTextField(
                            value = maxPriceText,
                            onValueChange = { value ->
                                maxPriceText = value
                                localFilters = localFilters.copy(
                                    maxPrice = value.toIntOrNull()
                                )
                            },
                            placeholder = {
                                Text(text = "до", fontSize = 13.sp, color = Color.Gray)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenColor,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(text = "Сбросить", fontSize = 15.sp)
                    }

                    Button(
                        onClick = { onApply(localFilters) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenColor
                        )
                    ) {
                        Text(
                            text = "Применить",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}