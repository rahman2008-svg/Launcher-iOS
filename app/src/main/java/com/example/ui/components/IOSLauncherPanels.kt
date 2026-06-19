package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LaunchableApp
import com.example.ui.LauncherViewModel
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

// 1. APPLE MUSIC CC MEDIA WIDGET
@Composable
fun CCMusicPlayerWidget(isDark: Boolean) {
    var isPlaying by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1F) else Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE0C3FC), Color(0xFF8EC5FC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Audio Controls
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Stargazing",
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Travis Scott - Astroworld",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Prev",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { }
                    )
                    
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "PlayPause",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { isPlaying = !isPlaying }
                    )
                    
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { }
                    )
                }
            }
        }
    }
}

// 2. CONTROL CENTER DRAWER OVERLAY PANEL
@Composable
fun ControlCenterPanel(
    viewModel: LauncherViewModel,
    onClose: () -> Unit
) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    val wifi = viewModel.isWifiEnabled.collectAsState().value
    val bluetooth = viewModel.isBluetoothEnabled.collectAsState().value
    val airplane = viewModel.isAirplaneMode.collectAsState().value
    val flashlight = viewModel.isFlashlightEnabled.collectAsState().value
    val brightness = viewModel.screenBrightness.collectAsState().value
    val volume = viewModel.volumeLevel.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onClose() } // Close when clicking background blur
    ) {
        // Blur glass card frame
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xEE161618) else Color(0xEEF2F2F7)),
            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clickable(enabled = false) {} // Prevent click-through closing
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Control Center",
                        color = if (isDark) Color.White else Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "CloseCC", tint = Color.Gray)
                    }
                }
                
                // Toggle Connections Grid & Control sliders row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Connection block
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF2C2C2E) else Color.White),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                    ) {
                        GridLayoutToggles(
                            isWifi = wifi, onWifiToggle = { viewModel.isWifiEnabled.value = !wifi },
                            isBlue = bluetooth, onBlueToggle = { viewModel.isBluetoothEnabled.value = !bluetooth },
                            isAir = airplane, onAirToggle = { viewModel.isAirplaneMode.value = !airplane },
                            isFlash = flashlight, onFlashToggle = { viewModel.isFlashlightEnabled.value = !flashlight }
                        )
                    }
                    
                    // Progressive level sliders layout block
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Brightness slider
                        CCVerticalSlider(
                            value = brightness,
                            onValueChange = { viewModel.screenBrightness.value = it },
                            icon = Icons.Default.WbSunny,
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        // Volume slider
                        CCVerticalSlider(
                            value = volume,
                            onValueChange = { viewModel.volumeLevel.value = it },
                            icon = Icons.Default.VolumeUp,
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Music Player inside CC
                CCMusicPlayerWidget(isDark)
            }
        }
    }
}

@Composable
fun CCVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF2C2C2E) else Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = "", tint = if (isDark) Color.White else Color.Black)
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFF007AFF),
                    inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f),
                    thumbColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            )
            
            Text(
                text = "${(value * 100).roundToInt()}%",
                color = if (isDark) Color.White else Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 4-Grid Connection togglers rendering
@Composable
fun GridLayoutToggles(
    isWifi: Boolean, onWifiToggle: () -> Unit,
    isBlue: Boolean, onBlueToggle: () -> Unit,
    isAir: Boolean, onAirToggle: () -> Unit,
    isFlash: Boolean, onFlashToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CCToggleButton(active = isWifi, onClick = onWifiToggle, activeColor = Color(0xFF007AFF), icon = Icons.Default.Wifi)
            CCToggleButton(active = isBlue, onClick = onBlueToggle, activeColor = Color(0xFF007AFF), icon = Icons.Default.Bluetooth)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CCToggleButton(active = isAir, onClick = onAirToggle, activeColor = Color(0xFFFF9500), icon = Icons.Default.AirplaneTicket)
            CCToggleButton(active = isFlash, onClick = onFlashToggle, activeColor = Color(0xFF4CD964), icon = Icons.Default.FlashlightOn)
        }
    }
}

@Composable
fun CCToggleButton(
    active: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (active) activeColor else Color.Gray.copy(alpha = 0.2f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = "", tint = if (active) Color.White else Color.DarkGray)
    }
}

// 3. SPOTLIGHT SEARCH OVERLAY
@Composable
fun SpotlightSearchPanel(
    viewModel: LauncherViewModel,
    apps: List<LaunchableApp>,
    onLaunch: (LaunchableApp) -> Unit
) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var queryText by remember { mutableStateOf("") }
    
    // Quick Math Resolver calculation state!
    val mathResult = remember(queryText) {
        resolveQuickMath(queryText)
    }

    // Filter app matches dynamically
    val matchedApps = remember(queryText, apps) {
        if (queryText.isEmpty()) {
            apps.take(4) // show recent suggestion shortcuts
        } else {
            apps.filter { it.label.contains(queryText, ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { viewModel.isSpotlightSearchOpen.value = false }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 64.dp)
                .clickable(enabled = false) {} // Prevent backdrop clicking
        ) {
            // Search Input Row card
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (queryText.isEmpty()) {
                            Text("Search apps, maps, arithmetic...", color = Color.Gray, fontSize = 16.sp)
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = if (isDark) Color.White else Color.Black, fontSize = 16.sp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (queryText.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Cancel, 
                            contentDescription = "Clear", 
                            tint = Color.Gray,
                            modifier = Modifier.clickable { queryText = "" }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // QUICK MATH CALCULATOR RESULTS CARD
            if (mathResult != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF2C2C2E) else Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9500)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("=", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Spotlight Calculator", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = mathResult, color = if (isDark) Color.White else Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            
            // Matches apps scroll list
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1F) else Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (queryText.isEmpty()) "SIRI SUGGESTIONS" else "BEST ACCORDING MATCHES",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                    )
                    
                    if (matchedApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No apps or contacts found.", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(matchedApps) { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onLaunch(app) }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom rendered iOS icon or package icon
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Gray.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (app.isSystemMock) {
                                            IOSIcon(name = app.systemIconName ?: "Settings", modifier = Modifier.fillMaxSize())
                                        } else {
                                            Icon(imageVector = Icons.Default.Apps, contentDescription = "", tint = Color.Gray)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = app.label, 
                                        color = if (isDark) Color.White else Color.Black, 
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun resolveQuickMath(input: String): String? {
    val clean = input.replace(" ", "")
    if (!clean.matches(Regex("^[0-9]+[+\\-*/][0-9]+$"))) return null
    
    return try {
        val operatorIndex = clean.indexOfFirst { it == '+' || it == '-' || it == '*' || it == '/' }
        val op = clean[operatorIndex]
        val num1 = clean.substring(0, operatorIndex).toDouble()
        val num2 = clean.substring(operatorIndex + 1).toDouble()
        
        val res = when (op) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            '*' -> num1 * num2
            '/' -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> 0.0
        }
        
        if (res.isNaN()) "Can't divide by zero"
        else if (res % 1.0 == 0.0) "${res.toInt()}"
        else String.format(Locale.US, "%.4f", res)
    } catch (e: Exception) {
        null
    }
}

// 4. ASSISTIVE TOUCH WIDGET
@Composable
fun AssistiveTouchWidget(
    viewModel: LauncherViewModel
) {
    var offsetX by remember { mutableStateOf(300f) }
    var offsetY by remember { mutableStateOf(1000f) }
    var isExpanded by remember { mutableStateOf(false) }
    val isDark = viewModel.isDarkMode.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isExpanded) {
            // Un-expanded small floating semi-transparent circular controller button
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                // Snap boundary logic (keep on sides of screen)
                                if (offsetX < size.width / 2) {
                                    offsetX = 20f
                                } else {
                                    offsetX = size.width - 180f
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .clickable { isExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                // Outer circle rings details
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.8f)))
                }
            }
        } else {
            // Full expanded assistive touch panel overlay card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable { isExpanded = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xF21C1C1E)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .size(240.dp)
                        .clickable(enabled = false) {} // Prevent click-through close
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Assistive items grid layout surrounding center dot
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                AssistCircleItem("Search", Icons.Default.Search) {
                                    viewModel.isSpotlightSearchOpen.value = true
                                    isExpanded = false
                                }
                                AssistCircleItem("Control", Icons.Default.SettingsApplications) {
                                    viewModel.isControlCenterOpen.value = true
                                    isExpanded = false
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                AssistCircleItem("Home", Icons.Default.Home) {
                                    viewModel.activeSubApp.value = null
                                    isExpanded = false
                                }
                                AssistCircleItem("Settings", Icons.Default.Settings) {
                                    viewModel.activeSubApp.value = "Settings"
                                    isExpanded = false
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                AssistCircleItem("Theme", Icons.Default.DarkMode) {
                                    viewModel.toggleSetting("dark_mode", true)
                                }
                                AssistCircleItem("Back", Icons.Default.Close) {
                                    isExpanded = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssistCircleItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Text(text = label, color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

// 5. MODULAR ACTIVE IOS HOME SCREEN WIDGETS
@Composable
fun WeatherWidget(isDark: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x33000000) else Color(0x66FFFFFF)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Cupertino", color = if (isDark) Color.White else Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Mostly Sunny", color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                Icon(imageVector = Icons.Default.WbSunny, contentDescription = "", tint = Color(0xFFFFCC00), modifier = Modifier.size(28.dp))
            }
            
            Text(text = "72°", color = if (isDark) Color.White else Color(0xFF0F172A), fontSize = 38.sp, fontWeight = FontWeight.Light)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "H:78°", color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "L:55°", color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BatteryWidget(isDark: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x33000000) else Color(0x66FFFFFF)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        modifier = Modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BATTERY STATUS", 
                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B), 
                fontSize = 9.sp, 
                fontWeight = FontWeight.Bold, 
                letterSpacing = 0.5.sp
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                // iPhone battery level
                CircularProgressWidget(percentage = 0.85f, label = "iPhone", color = Color(0xFF30D158), isDark = isDark)
                // AirPods battery level
                CircularProgressWidget(percentage = 0.90f, label = "Pods", color = Color(0xFF007AFF), isDark = isDark)
            }
        }
    }
}

@Composable
fun CircularProgressWidget(percentage: Float, label: String, color: Color, isDark: Boolean) {
    Column(
        modifier = Modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(46.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = percentage,
                color = color,
                strokeWidth = 3.5.dp,
                trackColor = Color.LightGray.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxSize()
            )
            Text(text = "${(percentage * 100).toInt()}%", color = if (isDark) Color.White else Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = label, color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ClockWidget(isDark: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x33000000) else Color(0x66FFFFFF)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "CLOCK DIAL", 
                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B), 
                fontSize = 9.sp, 
                fontWeight = FontWeight.Bold, 
                letterSpacing = 0.5.sp
            )
            
            // Render active mini clocks analog visual details
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(if (isDark) Color.Black else Color(0xFFF2F2F7)),
                contentAlignment = Alignment.Center
            ) {
                IOSIcon(name = "Clock", modifier = Modifier.fillMaxSize())
            }
            Text(
                text = "Cupertino Local Time", 
                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569), 
                fontSize = 8.sp, 
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CalendarWidget(isDark: Boolean, onClick: () -> Unit) {
    val calendar = Calendar.getInstance()
    val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "June"
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "Friday"

    Card(
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x33000000) else Color(0x66FFFFFF)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.24f)
                    .background(Color(0xFFFF3B30)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = dayOfWeek.uppercase(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.76f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = dayOfMonth, color = if (isDark) Color.White else Color(0xFF0F172A), fontSize = 38.sp, fontWeight = FontWeight.Light)
                Column {
                    Text(text = monthName, color = if (isDark) Color.White else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "1 event scheduled today", color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B), fontSize = 8.sp)
                }
            }
        }
    }
}
