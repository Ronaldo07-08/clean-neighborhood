package com.MobApp.cleanneighborhood.ui.map

import android.Manifest
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.MobApp.cleanneighborhood.R
import com.MobApp.cleanneighborhood.data.model.CollectionPoint
import com.MobApp.cleanneighborhood.data.model.MapFilters
import com.MobApp.cleanneighborhood.data.model.SortOrder
import com.MobApp.cleanneighborhood.data.model.WasteType
import com.MobApp.cleanneighborhood.data.model.WorkingHours
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

private val GreenColor = Color(0xFF609432)
private val GreenLight = Color(0xFFE8F5E9)
private val StarColor = Color(0xFFFFB800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    paddingValues: PaddingValues,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Запрос разрешения геолокации
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
                .padding(paddingValues)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Поиск + переключатель
            MapTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQuery = viewModel::onSearchQuery,
                isListMode = uiState.isListMode,
                onToggleMode = viewModel::toggleListMode,
                onFilterClick = viewModel::toggleFilterSheet,
                resultCount = uiState.filteredPoints.size
            )

            Box(modifier = Modifier.fillMaxSize()) {

                // Карта
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (!uiState.isListMode) 56.dp else 0.dp)
                        .then(
                            if (uiState.isListMode)
                                Modifier.size(0.dp)
                            else
                                Modifier.fillMaxSize()
                        )
                ) {
                    // Сама карта
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 1.5.dp,
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        GreenColor.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        GreenColor.copy(alpha = 0.5f)
                                    )
                                ),
                                shape = RoundedCornerShape(0.dp)
                            )
                    ) {
                        YandexMapView(
                            points = uiState.filteredPoints,
                            onPointSelected = viewModel::onPointSelected,
                            userLocation = uiState.userLocation,
                            locationUpdateCount = uiState.locationUpdateCount,
                            onMapTap = { viewModel.onPointSelected(null) }
                        )
                    }

                    uiState.selectedPoint?.let { point ->
                        PointPopup(
                            point = point,
                            onClose = { viewModel.onPointSelected(null) },
                            onDetailClick = { viewModel.openPointDetail(point) },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 72.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }

                // Список
                if (uiState.isListMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                    ) {
                        PointsList(
                            points = uiState.filteredPoints,
                            sortOrder = uiState.sortOrder,
                            onSortOrder = viewModel::setSortOrder,
                            onDetailClick = viewModel::openPointDetail
                        )
                    }
                }

                // Нижняя панель с кнопкой фильтры — только на карте
                if (!uiState.isListMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Кнопка фильтры слева
                        OutlinedButton(
                            onClick = viewModel::toggleFilterSheet,
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenColor
                            ),
                            border = BorderStroke(1.dp, GreenColor),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {

                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Фильтры",
                                fontSize = 14.sp,
                                color = GreenColor
                            )
                        }

                        // Кнопка геолокации справа
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GreenColor)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { viewModel.fetchUserLocation() }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_my_location),
                                contentDescription = "Моё местоположение",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottomsheet фильтров
        if (uiState.isFilterSheetOpen) {
            FilterBottomSheet(
                currentFilters = uiState.filters,
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
        // Детальный просмотр пункта
        uiState.detailPoint?.let { point ->
            PointDetailSheet(
                point = point,
                onClose = viewModel::closePointDetail
            )
        }
    }
}

// ─── Верхняя панель ───────────────────────────────────────────

@Composable
fun MapTopBar(
    searchQuery: String,
    onSearchQuery: (String) -> Unit,
    isListMode: Boolean,
    onToggleMode: () -> Unit,
    onFilterClick: () -> Unit,
    resultCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Строка поиска + переключатель в одной строке
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Поиск — занимает всё место кроме переключателя
            // Поиск — занимает всё место кроме переключателя
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQuery,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 13.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, end = 8.dp),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Найти пункт приёма...",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                )

                // Иконка поиска слева
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(18.dp)
                )
            }

            // Переключатель Карта/Список
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                    .height(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()

                        .background(
                            if (!isListMode) GreenColor else Color.White
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { if (isListMode) onToggleMode() }
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Карта",
                        fontSize = 13.sp,
                        color = if (!isListMode) Color.White else Color.Black,
                        fontWeight = if (!isListMode) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            if (isListMode) GreenColor else Color.White
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { if (!isListMode) onToggleMode() }
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Список",
                        fontSize = 13.sp,
                        color = if (isListMode) Color.White else Color.Black,
                        fontWeight = if (isListMode) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Найдено N пунктов — меньше и выше
        Text(
            text = "Найдено: $resultCount ${pluralPoints(resultCount)}",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

// ─── Яндекс карта ─────────────────────────────────────────────

@Composable
fun YandexMapView(
    points: List<CollectionPoint>,
    onPointSelected: (CollectionPoint?) -> Unit,
    userLocation: Pair<Double, Double>? = null,
    locationUpdateCount: Int = 0,
    onMapTap: () -> Unit = {}
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val tapListeners = remember { mutableListOf<MapObjectTapListener>() }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Слушатель нажатий на карту
    val inputListener = remember {
        object : com.yandex.mapkit.map.InputListener {
            override fun onMapTap(map: com.yandex.mapkit.map.Map, point: Point) {
                // Нажатие на свободную часть карты — закрываем карточку
                onMapTap()
            }

            override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: Point) {
                onMapTap()
            }
        }
    }

    // Слушатель движения камеры — срабатывает при свайпе
    val cameraListener = remember {
        com.yandex.mapkit.map.CameraListener { _, _, cameraUpdateReason, _ ->
            // GESTURES — означает что камера двигается из-за жеста пользователя
            if (cameraUpdateReason == com.yandex.mapkit.map.CameraUpdateReason.GESTURES) {
                onMapTap()
            }
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).also { view ->
                mapView = view
                MapKitFactory.getInstance().onStart()
                view.onStart()


                view.mapWindow.map.addInputListener(inputListener)
                view.mapWindow.map.addCameraListener(cameraListener)


                view.mapWindow.map.move(
                    CameraPosition(
                        Point(56.838011, 60.597474),
                        12.0f, 0.0f, 0.0f
                    )
                )

                view.mapWindow.map.logo.setAlignment(
                    com.yandex.mapkit.logo.Alignment(
                        com.yandex.mapkit.logo.HorizontalAlignment.RIGHT,
                        com.yandex.mapkit.logo.VerticalAlignment.BOTTOM
                    )
                )
                view.mapWindow.map.logo.setPadding(
                    com.yandex.mapkit.logo.Padding(0, 60)
                )
            }
        },
        update = { view ->
            view.mapWindow.map.mapObjects.clear()
            tapListeners.clear()

            // Маркеры пунктов
            points.forEach { point ->
                val mapObject = view.mapWindow.map.mapObjects.addPlacemark().apply {
                    geometry = Point(point.latitude, point.longitude)
                    setIcon(
                        ImageProvider.fromResource(
                            view.context,
                            R.drawable.ic_map_marker
                        )
                    )
                }
                val listener = MapObjectTapListener { _, _ ->
                    onPointSelected(point)
                    true
                }
                tapListeners.add(listener)
                mapObject.addTapListener(listener)
            }

            // Маркер пользователя
            userLocation?.let { (lat, lon) ->
                android.util.Log.d("MapView", "Drawing user marker at $lat $lon")
                view.mapWindow.map.mapObjects.addPlacemark().apply {
                    geometry = Point(lat, lon)
                    setIcon(
                        ImageProvider.fromResource(
                            view.context,
                            R.drawable.ic_my_location
                        ),

                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(locationUpdateCount) {
        if (locationUpdateCount > 0 && userLocation != null) {
            // Небольшая задержка чтобы карта успела инициализироваться
            kotlinx.coroutines.delay(100)
            android.util.Log.d("MapView", "Moving camera, count=$locationUpdateCount")
            mapView?.mapWindow?.map?.move(
                CameraPosition(
                    Point(userLocation.first, userLocation.second),
                    14.0f, 0.0f, 0.0f
                )
            )
        }
    }



    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView?.onStart()
                }
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> {
                    mapView?.onStop()
                    MapKitFactory.getInstance().onStop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            mapView?.mapWindow?.map?.removeCameraListener(cameraListener)
            mapView?.mapWindow?.map?.removeInputListener(inputListener)
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView?.onStop()
        }
    }
}

// ─── Popup пункта на карте ────────────────────────────────────

@Composable
fun PointPopup(
    point: CollectionPoint,
    onClose: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Название
                Text(
                    text = point.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Кнопка закрыть
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Рейтинг
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${point.rating}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${point.reviewCount} оценок",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Адрес
            Text(
                text = point.address,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Расписание
            Text(
                text = point.schedule,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Открыто/Закрыто
                Text(
                    text = if (point.isOpen) "открыто" else "закрыто",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (point.isOpen) GreenColor else Color.Red
                )

                // Кнопка перехода к деталям
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GreenColor)
                        .clickable { onDetailClick()},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Подробнее",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─── Список пунктов ───────────────────────────────────────────

@Composable
fun PointsList(
    points: List<CollectionPoint>,
    sortOrder: SortOrder,
    onSortOrder: (SortOrder) -> Unit,
    onDetailClick: (CollectionPoint) -> Unit
) {
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Дропдаун сортировки
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box {
                OutlinedButton(
                    onClick = { sortExpanded = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sortOrder.label,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    SortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = order.label,
                                    color = if (order == sortOrder)
                                        GreenColor
                                    else
                                        Color.Black
                                )
                            },
                            onClick = {
                                onSortOrder(order)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Список карточек
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(points) { point ->
                PointCard(point = point,
                    onDetailClick = { onDetailClick(point) }
                )

            }
        }
    }
}

// ─── Карточка пункта в списке ─────────────────────────────────

@Composable
fun PointCard(point: CollectionPoint,
              onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Название
            Text(
                text = point.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Рейтинг
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${point.rating}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${point.reviewCount} оценок",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Адрес
            Text(
                text = point.address,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Расписание
            Text(
                text = point.schedule,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Открыто/Закрыто
                Text(
                    text = if (point.isOpen) "открыто" else "закрыто",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (point.isOpen) GreenColor else Color.Red
                )

                // Кнопка Подробнее
                Button(
                    onClick = onDetailClick,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenColor
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Подробнее",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Bottomsheet фильтров ─────────────────────────────────────

@Composable
fun FilterBottomSheet(
    currentFilters: MapFilters,
    onApply: (MapFilters) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var localFilters by remember { mutableStateOf(currentFilters) }

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
                // Заголовок
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

                    WasteType.entries.forEach { wasteType ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = wasteType in localFilters.wasteTypes,
                                onCheckedChange = { checked ->
                                    localFilters = localFilters.copy(
                                        wasteTypes = if (checked)
                                            localFilters.wasteTypes + wasteType
                                        else
                                            localFilters.wasteTypes - wasteType
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GreenColor,
                                    uncheckedColor = Color.Gray
                                )
                            )
                            Text(
                                text = wasteType.label,
                                fontSize = 14.sp
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // Радиус
                    Text(
                        text = "Радиус от меня, км",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = localFilters.radiusKm,
                            onValueChange = { value ->
                                localFilters = localFilters.copy(radiusKm = value)
                            },
                            valueRange = 0f..50f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = GreenColor,
                                activeTrackColor = GreenColor
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = localFilters.radiusKm.toInt().toString(),
                            fontSize = 14.sp,
                            modifier = Modifier.width(24.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // Рейтинг
                    Text(
                        text = "Рейтинг от",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = localFilters.minRating,
                            onValueChange = { value ->
                                localFilters = localFilters.copy(minRating = value)
                            },
                            valueRange = 0f..5f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = GreenColor,
                                activeTrackColor = GreenColor
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("%.1f", localFilters.minRating),
                            fontSize = 14.sp,
                            modifier = Modifier.width(24.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    // Время работы
                    Text(
                        text = "Время работы",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        WorkingHours.OPEN_NOW to "открыто сейчас",
                        WorkingHours.AROUND_THE_CLOCK to "круглосуточно",
                        WorkingHours.OPEN_AT_SPECIFIED_TIME to "открыто в указанное время"
                    ).forEach { (value, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = localFilters.workingHours == value,
                                onClick = {
                                    localFilters = localFilters.copy(
                                        workingHours = if (localFilters.workingHours == value)
                                            null
                                        else
                                            value
                                    )
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GreenColor
                                )
                            )
                            Text(
                                text = label,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Сбросить
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

                    // Применить
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

// ─── Вспомогательные функции ──────────────────────────────────

fun pluralPoints(count: Int): String {
    return when {
        count % 100 in 11..19 -> "пунктов"
        count % 10 == 1 -> "пункт"
        count % 10 in 2..4 -> "пункта"
        else -> "пунктов"
    }
}