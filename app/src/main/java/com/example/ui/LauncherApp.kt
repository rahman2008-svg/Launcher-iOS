package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.data.database.LauncherWidget
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun LauncherApp(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val apps by viewModel.appsList.collectAsState()
    val widgets by viewModel.widgetsList.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val isJiggle by viewModel.isJiggleMode.collectAsState()
    val isCCPanel by viewModel.isControlCenterOpen.collectAsState()
    val isSearchPanel by viewModel.isSpotlightSearchOpen.collectAsState()
    val isAssistiveTouch by viewModel.isAssistiveTouchOpen.collectAsState()
    val activeSubApp by viewModel.activeSubApp.collectAsState()
    val activeWallpaper by viewModel.activeWallpaper.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    // Resolve custom generated wallpaper IDs dynamically using string lookups to avoid crash
    val classicWallpaperResId = remember {
        val id = context.resources.getIdentifier("img_ios_wallpaper_classic_1781855487184", "drawable", context.packageName)
        if (id != 0) id else null
    }

    val darkWallpaperResId = remember {
        val id = context.resources.getIdentifier("img_ios_wallpaper_dark_1781855503693", "drawable", context.packageName)
        if (id != 0) id else null
    }

    // Dynamic clock / time status header bar
    var currentTimeString by remember { mutableStateOf("00:00") }
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            currentTimeString = sdf.format(Date())
            kotlinx.coroutines.delay(1000 * 30) // Update every 30 seconds
        }
    }

    // iOS Wiggle Motion animation sequence
    val infiniteTransition = rememberInfiniteTransition(label = "JiggleTransit")
    val wiggleZ by infiniteTransition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(110, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "JiggleRot"
    )

    // Layout Root
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. DYNAMIC WALLPAPER BASE LAYER
        if (activeWallpaper == "vibrant") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF2B3A55), Color(0xFF5A447A))
                            } else {
                                listOf(Color(0xFF8EC5FC), Color(0xFFE0C3FC))
                            }
                        )
                    )
            )
        } else if (activeWallpaper == "dark" && darkWallpaperResId != null) {
            Image(
                painter = painterResource(id = darkWallpaperResId),
                contentDescription = "Wallpaper Dark",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (activeWallpaper == "classic" && classicWallpaperResId != null) {
            Image(
                painter = painterResource(id = classicWallpaperResId),
                contentDescription = "Wallpaper Classic",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // High fidelity procedurally drawn fallback gradient background ("vibrant" palette style)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF2B3A55), Color(0xFF5A447A))
                            } else {
                                listOf(Color(0xFF8EC5FC), Color(0xFFE0C3FC))
                            }
                        )
                    )
            )
        }

        // 2. MAIN HUB RENDER (When no simulated sub-app is running)
        AnimatedContent(
            targetState = activeSubApp,
            transitionSpec = {
                (slideInVertically { it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut())
                    .using(SizeTransform(clip = false))
            },
            label = "ScreenSwitch"
        ) { activeApp ->
            if (activeApp == null) {
                // RENDER MAIN LAUNCHER PAGES
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    
                    // TOP APPLE HEURISTIC BAR (Clicking opens Control Center)
                    val statusBarColor = if (isDark) Color.White else Color(0xFF0F172A)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.isControlCenterOpen.value = true }
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentTimeString,
                            color = statusBarColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("status_bar_clock")
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = "", tint = statusBarColor, modifier = Modifier.size(14.dp))
                            Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = "", tint = statusBarColor, modifier = Modifier.size(16.dp))
                            Text(text = "85%", color = statusBarColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // EDIT MODE SUB TOOLBAR (Showing Done & widget spawn indicators)
                    if (isJiggle) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Add Widget Trigger button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .clickable {
                                        viewModel.addWidget("WEATHER", 0)
                                        Toast.makeText(context, "Added Weather Widget to page!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = "AddWidget", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Widget", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            // Exit Editmode Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.35f))
                                    .clickable { viewModel.isJiggleMode.value = false }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Done", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    // MAIN PAGE VIEWER (Horizontal scrolling between Screen Panels)
                    val pagerState = rememberPagerState(pageCount = { 3 })
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
                                // Detect large vertical swipes to open Spotlight Search!
                                detectDragGestures { change, dragAmount ->
                                    if (dragAmount.y > 60f) {
                                        viewModel.isSpotlightSearchOpen.value = true
                                    }
                                }
                            }
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> FirstLauncherPage(apps, widgets, isJiggle, wiggleZ, viewModel)
                                1 -> SecondaryLauncherPage(apps, isJiggle, wiggleZ, viewModel)
                                2 -> AppLibraryPage(apps, viewModel)
                            }
                        }
                    }
                    
                    // BOTTOM DOT INDICATORS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeDotColor = if (isDark) Color.White else Color(0xFF0F172A)
                        val inactiveDotColor = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF0F172A).copy(alpha = 0.2f)
                        for (i in 0..2) {
                            val active = pagerState.currentPage == i
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (active) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (active) activeDotColor else inactiveDotColor)
                            )
                        }
                    }

                    // BOTTOM STICKY FLOATING GLASS DOCK (Fixed favorite launches)
                    LauncherBottomDockTray(apps, isJiggle, wiggleZ, viewModel)
                }
            } else {
                // RENDER SIMULATED SUB APPLICATIONS
                when (activeApp) {
                    "Safari" -> SafariSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Weather" -> WeatherSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Photos" -> PhotosSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "AppStore" -> AppStoreSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Settings" -> SettingsSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Camera" -> CameraSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Maps" -> MapsSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Calendar" -> CalendarSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Clock" -> ClockSubApp(viewModel) { viewModel.activeSubApp.value = null }
                    "Health" -> SimulatedAppContainer("Health", viewModel, { viewModel.activeSubApp.value = null }) {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "", tint = Color(0xFFFF2D55), modifier = Modifier.size(72.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Health Metrics", color = if (isDark) Color.White else Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("Step goal: 8,500\nActive calories: 420 kcal\nHeart Rate: 72 bpm", color = Color.Gray, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp))
                            }
                        }
                    }
                }
            }
        }

        // 3. OVERLAYS (Control Center, Search overlays, Assistive touch panel)
        AnimatedVisibility(
            visible = isCCPanel,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.zIndex(100f) // Keep above pages
        ) {
            ControlCenterPanel(viewModel, onClose = { viewModel.isControlCenterOpen.value = false })
        }

        AnimatedVisibility(
            visible = isSearchPanel,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
            modifier = Modifier.zIndex(90f)
        ) {
            SpotlightSearchPanel(viewModel, apps, onLaunch = { viewModel.launchApp(context, it) })
        }

        // Draggable Assistive Touch bubble floating overlay
        if (isAssistiveTouch) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(150f)
            ) {
                AssistiveTouchWidget(viewModel)
            }
        }
    }
}

// FIRST HOME PAGE - MIX OF ACTIVE WIDGETS AND APPS
@Composable
fun FirstLauncherPage(
    apps: List<LaunchableApp>,
    widgets: List<LauncherWidget>,
    isJiggle: Boolean,
    wiggleAngle: Float,
    viewModel: LauncherViewModel
) {
    val pageApps = apps.filter { it.pageIndex == 0 && !it.isFavorite }
    val pageWidgets = widgets.filter { it.pageIndex == 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Render 2x2 Widgets top shelf
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weather mini widget
            Box(modifier = Modifier.weight(1f)) {
                WeatherWidget(isDark = viewModel.isDarkMode.collectAsState().value) {
                    viewModel.activeSubApp.value = "Weather"
                }
                if (isJiggle) {
                    WidgetDeleteBadge {
                        val widgetEntity = pageWidgets.find { it.type == "WEATHER" }
                        if (widgetEntity != null) viewModel.removeWidget(widgetEntity.id)
                    }
                }
            }
            // Battery Status circle widget
            Box(modifier = Modifier.weight(1f)) {
                BatteryWidget(isDark = viewModel.isDarkMode.collectAsState().value)
                if (isJiggle) {
                    WidgetDeleteBadge {
                        val widgetEntity = pageWidgets.find { it.type == "BATTERY" }
                        if (widgetEntity != null) viewModel.removeWidget(widgetEntity.id)
                    }
                }
            }
        }

        // Render Apps Grid below widgets
        Box(modifier = Modifier.fillMaxWidth()) {
            FlowLayoutGrid(
                items = pageApps,
                isJiggle = isJiggle,
                wiggleAngle = wiggleAngle,
                onLaunch = { app -> viewModel.launchApp(viewModel.getApplication(), app) },
                onLongPress = { viewModel.isJiggleMode.value = true },
                onDelete = { app -> viewModel.hideOrUninstallApp(app.packageName) }
            )
        }
    }
}

// SECOND HOME PAGE - FULL COG GRID APPS
@Composable
fun SecondaryLauncherPage(
    apps: List<LaunchableApp>,
    isJiggle: Boolean,
    wiggleAngle: Float,
    viewModel: LauncherViewModel
) {
    val pageApps = apps.filter { it.pageIndex == 1 && !it.isFavorite }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
    ) {
        FlowLayoutGrid(
            items = pageApps,
            isJiggle = isJiggle,
            wiggleAngle = wiggleAngle,
            onLaunch = { app -> viewModel.launchApp(viewModel.getApplication(), app) },
            onLongPress = { viewModel.isJiggleMode.value = true },
            onDelete = { app -> viewModel.hideOrUninstallApp(app.packageName) }
        )
    }
}

// REUSABLE APPS FLOW GRID CELL LAYOUTS
@Composable
fun FlowLayoutGrid(
    items: List<LaunchableApp>,
    isJiggle: Boolean,
    wiggleAngle: Float,
    onLaunch: (LaunchableApp) -> Unit,
    onLongPress: () -> Unit,
    onDelete: (LaunchableApp) -> Unit
) {
    // Elegant grid math arrangements
    Column {
        val rows = items.chunked(4)
        rows.forEach { rowApps ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0..3) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (col < rowApps.size) {
                            val app = rowApps[col]
                            AppIconMainGridElement(app, isJiggle, wiggleAngle, onLaunch, onLongPress, onDelete)
                        } else {
                            Spacer(modifier = Modifier.size(60.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppIconMainGridElement(
    app: LaunchableApp,
    isJiggle: Boolean,
    wiggleAngle: Float,
    onLaunch: (LaunchableApp) -> Unit,
    onLongPress: () -> Unit,
    onDelete: (LaunchableApp) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .graphicsLayer {
                // Apply iOS wiggle rotation around center
                rotationZ = if (isJiggle) wiggleAngle else 0f
            }
            .pointerInput(app) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { if (!isJiggle) onLaunch(app) }
                )
            }
    ) {
        Box(
            modifier = Modifier.size(56.dp)
        ) {
            // Render squircle app drawing
            if (app.isSystemMock) {
                IOSIcon(name = app.systemIconName ?: "Settings", modifier = Modifier.fillMaxSize())
            } else {
                // Physical package native bitmap display
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.drawable != null) {
                        Image(
                            painter = remember(app.drawable) {
                                androidx.compose.ui.graphics.painter.BitmapPainter(
                                    drawableToBitmap(app.drawable!!).asImageBitmap()
                                )
                            },
                            contentDescription = app.label,
                            modifier = Modifier.fillMaxSize(0.70f)
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Apps, contentDescription = "", tint = Color.Gray)
                    }
                }
            }
            
            // Render minus uninstall badge buttons
            if (isJiggle) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset((-4).dp, (-4).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .clickable { onDelete(app) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            lineHeight = 12.sp
        )
    }
}

// APP LIBRARY PAGE (Group categorizations)
@Composable
fun AppLibraryPage(
    apps: List<LaunchableApp>,
    viewModel: LauncherViewModel
) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var librarySearchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Categorization boxes lists
    val categorizations = remember(apps, librarySearchText) {
        val filtered = if (librarySearchText.isEmpty()) apps else apps.filter { it.label.contains(librarySearchText, ignoreCase = true) }
        listOf(
            "Social Media" to filtered.filter { it.packageName.contains("social") || it.label.contains("whatsapp") || it.label.contains("insta") || it.label.contains("tok") },
            "Utilities" to filtered.filter { it.packageName.contains("setting") || it.packageName.contains("camera") || it.packageName.contains("safari") },
            "Entertainment" to filtered.filter { it.label.contains("netflix") || it.label.contains("youtube") || it.label.contains("spot") || it.label.contains("music") },
            "Information" to filtered.filter { it.label.contains("weather") || it.label.contains("calendar") || it.label.contains("maps") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "App Library",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Search filter input
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = librarySearchText, 
                    onValueChange = { librarySearchText = it }, 
                    textStyle = androidx.compose.ui.text.TextStyle(color = if (isDark) Color.White else Color.Black),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categorizations) { cat ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .height(140.dp)
                        .padding(bottom = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = cat.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        // Small 2x2 cluster icons inside the Library Folder Card
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            cat.second.take(4).forEach { app ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.launchApp(context, app) }
                                ) {
                                    if (app.isSystemMock) {
                                        IOSIcon(name = app.systemIconName ?: "Settings", modifier = Modifier.fillMaxSize())
                                    } else {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.Apps, contentDescription = "", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// STICKY BOTTOM FIXED BAR DOCK HOLDER
@Composable
fun LauncherBottomDockTray(
    apps: List<LaunchableApp>,
    isJiggle: Boolean,
    wiggleAngle: Float,
    viewModel: LauncherViewModel
) {
    // Dock matches apps marked isFavorite, capped at 4
    val dockApps = apps.filter { it.isFavorite }.take(4)
    val fallbackDockApps = remember(apps) {
        if (dockApps.isEmpty()) {
            apps.filter { it.packageName.contains("safari") || it.packageName.contains("store") || it.packageName.contains("camera") || it.packageName.contains("setting") || it.packageName.contains("clock") }.take(4)
        } else {
            dockApps
        }
    }

    val isDark = viewModel.isDarkMode.collectAsState().value
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(if (isDark) Color.Black.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.35f))
            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(36.dp))
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            fallbackDockApps.forEach { app ->
                AppIconMainGridElement(
                    app = app,
                    isJiggle = isJiggle,
                    wiggleAngle = wiggleAngle,
                    onLaunch = { viewModel.launchApp(viewModel.getApplication(), it) },
                    onLongPress = { viewModel.isJiggleMode.value = true },
                    onDelete = { viewModel.hideOrUninstallApp(it.packageName) }
                )
            }
        }
    }
}

@Composable
fun WidgetDeleteBadge(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .offset((-4).dp, (-4).dp)
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.Red)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("-", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// Custom fail-safe helper to convert any Android drawable (including vectors) into a Bitmap
fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): android.graphics.Bitmap {
    if (drawable is android.graphics.drawable.BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap = android.graphics.Bitmap.createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(1),
        drawable.intrinsicHeight.coerceAtLeast(1),
        android.graphics.Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
