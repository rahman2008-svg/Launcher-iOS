package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.LauncherViewModel
import com.example.ui.StoreApp
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun SimulatedAppContainer(
    appName: String,
    viewModel: LauncherViewModel,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0F0F10) else Color(0xFFF2F2F7))
    ) {
        // App Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp) // Leave space for iOS Home Indicator bar at the bottom
        ) {
            content()
        }
        
        // Solid iOS bottom home indicator slider button to exit app
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(24.dp)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.5f))
            )
        }
    }
}

// 1. SAFARI SUB APP
@Composable
fun SafariSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    var urlText by remember { mutableStateOf("https://www.google.com") }
    var loadedPageTitle by remember { mutableStateOf("Google") }
    var pageContent by remember { mutableStateOf("Welcome to simulated Safari. Search anything or tap quick link buttons below!") }

    SimulatedAppContainer("Safari", viewModel, onClose) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Safari URL toolbar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1C1E))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2C2C2E))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Secure", tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    BasicTextSecure(
                        value = urlText,
                        onValueChange = { urlText = it },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh, 
                        contentDescription = "Refresh", 
                        tint = Color.Gray, 
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                if (urlText.contains("google")) {
                                    loadedPageTitle = "Google"
                                    pageContent = "Standard Google Search Engine simulation page."
                                } else {
                                    loadedPageTitle = "Web Reader"
                                    pageContent = "Parsed reader mode view of $urlText."
                                }
                            }
                    )
                }
            }

            // Web canvas pane
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = loadedPageTitle,
                        color = Color.Black,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = pageContent,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Quick Favorites",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val quickLinks = listOf(
                            Triple("Apple", "https://apple.com", Icons.Default.Devices),
                            Triple("Google", "https://google.com", Icons.Default.Search),
                            Triple("AI Studio", "https://ai.studio", Icons.Default.Memory),
                            Triple("Email", "https://gmail.com", Icons.Default.Email)
                        )
                        
                        quickLinks.forEach { link ->
                            Column(
                                modifier = Modifier
                                    .clickable {
                                        urlText = link.second
                                        loadedPageTitle = link.first
                                        pageContent = "You have navigated to ${link.first}! This is a gorgeous custom page representing ${link.second}."
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF2F2F7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = link.third, contentDescription = link.first, tint = Color(0xFF007AFF), modifier = Modifier.size(24.dp))
                                }
                                Text(text = link.first, color = Color.Black, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper Text editor wrapper
@Composable
fun BasicTextSecure(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        if (value.isEmpty()) {
            Text("Search or enter website", color = Color.Gray, fontSize = 14.sp)
        }
        BasicTextFieldSecure(value, onValueChange, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun BasicTextFieldSecure(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
        singleLine = true,
        modifier = modifier
    )
}

// 2. WEATHER SUB APP
@Composable
fun WeatherSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    SimulatedAppContainer("Weather", viewModel, onClose) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF5AC8FA), Color(0xFF007AFF), Color(0xFF5856D6))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Cupertino", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Normal)
                Text(text = "72°", color = Color.White, fontSize = 96.sp, fontWeight = FontWeight.Thin)
                Text(text = "Mostly Sunny", color = Color.White.copy(alpha = 0.8f), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "H:78°", color = Color.White, fontSize = 16.sp)
                    Text(text = "L:55°", color = Color.White, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Hourly Forecast Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sunny conditions will continue. Wind gusts up to 12 mph.",
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Divider(color = Color.White.copy(alpha = 0.2f))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            val items = listOf<Triple<String, String, ImageVector>>(
                                Triple("Now", "72°", Icons.Default.WbSunny),
                                Triple("1 PM", "75°", Icons.Default.WbSunny),
                                Triple("2 PM", "77°", Icons.Default.WbSunny),
                                Triple("3 PM", "76°", Icons.Default.WbSunny),
                                Triple("4 PM", "72°", Icons.Default.WbCloudy),
                                Triple("5 PM", "68°", Icons.Default.WbCloudy)
                            )
                            items.forEach { forecasts ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = forecasts.first, color = Color.White, fontSize = 14.sp)
                                    Icon(imageVector = forecasts.third, contentDescription = "", tint = Color.Yellow, modifier = Modifier.size(24.dp).padding(vertical = 6.dp))
                                    Text(text = forecasts.second, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 5-Day Forecast Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "5-DAY FORECAST", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val days = listOf<Triple<String, String, ImageVector>>(
                            Triple("Today", "78° / 55°", Icons.Default.WbSunny),
                            Triple("Sat", "81° / 58°", Icons.Default.WbSunny),
                            Triple("Sun", "74° / 52°", Icons.Default.WbCloudy),
                            Triple("Mon", "70° / 49°", Icons.Default.Thunderstorm),
                            Triple("Tue", "76° / 53°", Icons.Default.WbSunny)
                        )
                        
                        days.forEachIndexed { i, day ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = day.first, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(70.dp))
                                Icon(imageVector = day.third, contentDescription = "", tint = if (day.first.contains("Mon")) Color.LightGray else Color.Yellow, modifier = Modifier.size(24.dp))
                                Text(text = day.second, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            if (i < days.size - 1) {
                                Divider(color = Color.White.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. PHOTOS SUB APP
@Composable
fun PhotosSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var selectedPhotoId by remember { mutableStateOf<Int?>(null) }
    
    val photoColors = listOf(
        Color(0xFFFF3B30), Color(0xFFFF9500), Color(0xFFFFCC00), Color(0xFF4CD964),
        Color(0xFF5AC8FA), Color(0xFF007AFF), Color(0xFF5856D6), Color(0xFFFF2D55),
        Color(0xFF8E8E93), Color(0xFF1C1C1E), Color(0xFF2C2C2E), Color(0xFF3A3A3C)
    )

    SimulatedAppContainer("Photos", viewModel, onClose) {
        if (selectedPhotoId == null) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "Library",
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(photoColors) { index, color ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(color, color.copy(alpha = 0.5f))
                                    )
                                )
                                .clickable { selectedPhotoId = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = "", tint = Color.White.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        } else {
            // Full screen Single Photo Viewer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(photoColors[selectedPhotoId!!], Color.Black)
                            )
                        )
                )
                
                // Back button
                IconButton(
                    onClick = { selectedPhotoId = null },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
                Text(
                    text = "Photo #${selectedPhotoId!! + 1}\nCupertino Classic Gradient Artwork",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(24.dp)
                )
            }
        }
    }
}

// 4. CALENDAR SUB APP
@Composable
fun CalendarSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var newEventText by remember { mutableStateOf("") }
    val eventsList = viewModel.mockEvents

    SimulatedAppContainer("Calendar", viewModel, onClose) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "June 2026",
                color = Color(0xFFFF3C30), // iOS red calendar branding
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Render simplified visual month grid card
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.1f))
                    
                    // Simple Calendar days grid block
                    var dayCount = 1
                    for (row in 0..4) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 0..6) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val isToday = dayCount == 19 // Local time metadata indicates June 19th!
                                    if (dayCount <= 30) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (isToday) Color(0xFFFF3B30) else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayCount",
                                                color = if (isToday) Color.White else (if (isDark) Color.White else Color.Black),
                                                fontSize = 14.sp,
                                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                        dayCount++
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Events list
            Text(
                text = "UPCOMING EVENTS",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Add custom items
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newEventText,
                    onValueChange = { newEventText = it },
                    placeholder = { Text("Add New Schedule Item...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                        unfocusedContainerColor = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newEventText.isNotEmpty()) {
                            viewModel.addCalendarEvent(newEventText)
                            newEventText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+")
                }
            }
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(eventsList) { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFFF3B30))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = event,
                            color = if (isDark) Color.White else Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// 5. CLOCK SUB APP
@Composable
fun ClockSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var selectedTab by remember { mutableStateOf(0) } // 0: World Clock, 1: Alarm, 2: Stopwatch, 3: Timer

    // Stopwatch variables
    var stopwatchTime by remember { mutableStateOf(0L) }
    var isStopwatchRunning by remember { mutableStateOf(false) }
    LaunchedEffect(isStopwatchRunning) {
        while (isStopwatchRunning) {
            delay(10)
            stopwatchTime += 10
        }
    }

    // Timer variables
    var totalTimerSeconds by remember { mutableStateOf(60) }
    var remainingTimerSeconds by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingTimerSeconds > 0) {
            delay(1000)
            remainingTimerSeconds--
            if (remainingTimerSeconds == 0) {
                isTimerRunning = false
            }
        }
    }

    SimulatedAppContainer("Clock", viewModel, onClose) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Top Content Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> { // World Clock
                        Column {
                            Text(text = "World Clock", color = if (isDark) Color.White else Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                            val zones = listOf(
                                "Cupertino" to "Today, -3 hrs",
                                "Tokyo" to "Tomorrow, +16 hrs",
                                "London" to "Today, +8 hrs",
                                "Sydney" to "Tomorrow, +18 hrs"
                            )
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(zones) { zone ->
                                    val calendar = Calendar.getInstance()
                                    if (zone.first == "Tokyo") calendar.add(Calendar.HOUR, 16)
                                    else if (zone.first == "London") calendar.add(Calendar.HOUR, 8)
                                    else if (zone.first == "Sydney") calendar.add(Calendar.HOUR, 18)
                                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                    val min = String.format("%02d", calendar.get(Calendar.MINUTE))
                                    val formattedTime = "${if (hour > 12) hour-12 else if (hour==0) 12 else hour}:$min ${if (hour>=12) "PM" else "AM"}"

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = zone.first, color = if (isDark) Color.White else Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = zone.second, color = Color.Gray, fontSize = 12.sp)
                                        }
                                        Text(text = formattedTime, color = if (isDark) Color.White else Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Light)
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Alarm
                        Column {
                            Text(text = "Alarm", color = if (isDark) Color.White else Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                            val alarms = listOf(
                                "07:00 AM" to "Weekdays",
                                "09:30 AM" to "Weekends"
                            )
                            alarms.forEach { alarm ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = alarm.first, color = if (isDark) Color.White else Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Thin)
                                        Text(text = alarm.second, color = Color.Gray, fontSize = 14.sp)
                                    }
                                    var activeState by remember { mutableStateOf(true) }
                                    Switch(checked = activeState, onCheckedChange = { activeState = it }, colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF34C759)))
                                }
                                Divider(color = Color.Gray.copy(alpha = 0.2f))
                            }
                        }
                    }
                    2 -> { // Stopwatch
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val min = (stopwatchTime / 60000) % 60
                            val sec = (stopwatchTime / 1000) % 60
                            val milli = (stopwatchTime / 10) % 100
                            val formattedTime = String.format("%02d:%02d.%02d", min, sec, milli)

                            Text(
                                text = formattedTime,
                                color = if (isDark) Color.White else Color.Black,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.ExtraLight,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 40.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Lap / Reset button
                                Box(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                                        .clickable {
                                            isStopwatchRunning = false
                                            stopwatchTime = 0
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Reset", color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.SemiBold)
                                }
                                
                                // Start / Stop button
                                Box(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .clip(CircleShape)
                                        .background(if (isStopwatchRunning) Color(0xFFFF453A).copy(alpha = 0.2f) else Color(0xFF30D158).copy(alpha = 0.2f))
                                        .clickable { isStopwatchRunning = !isStopwatchRunning },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isStopwatchRunning) "Stop" else "Start",
                                        color = if (isStopwatchRunning) Color(0xFFFF453A) else Color(0xFF30D158),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    3 -> { // Timer
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val displayMin = remainingTimerSeconds / 60
                            val displaySec = remainingTimerSeconds % 60
                            
                            Box(
                                modifier = Modifier.size(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Circular loading timer ring
                                CircularProgressIndicator(
                                    progress = { remainingTimerSeconds.toFloat() / totalTimerSeconds },
                                    color = Color(0xFFFF9500),
                                    strokeWidth = 6.dp,
                                    trackColor = Color.Gray.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = String.format("%02d:%02d", displayMin, displaySec),
                                    color = if (isDark) Color.White else Color.Black,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Thin
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(40.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        isTimerRunning = false
                                        remainingTimerSeconds = 60
                                        totalTimerSeconds = 60
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)),
                                    shape = CircleShape,
                                    modifier = Modifier.size(75.dp).padding(0.dp)
                                ) {
                                    Text("Cancel", color = if (isDark) Color.White else Color.Black, fontSize = 13.sp)
                                }
                                
                                Button(
                                    onClick = { isTimerRunning = !isTimerRunning },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500)),
                                    shape = CircleShape,
                                    modifier = Modifier.size(75.dp)
                                ) {
                                    Text(if (isTimerRunning) "Pause" else "Start", color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
            
            // Tab Bottom Bar selector
            NavigationBar(containerColor = if (isDark) Color(0xFF161618) else Color(0xFFE5E5EA)) {
                val tabs = listOf<Triple<String, ImageVector, Int>>(
                    Triple("World Clock", Icons.Default.Public, 0),
                    Triple("Alarm", Icons.Default.Alarm, 1),
                    Triple("Stopwatch", Icons.Default.Timer, 2),
                    Triple("Timer", Icons.Default.HourglassEmpty, 3)
                )
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab.third,
                        onClick = { selectedTab = tab.third },
                        icon = { Icon(imageVector = tab.second, contentDescription = tab.first) },
                        label = { Text(text = tab.first, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF9500),
                            selectedTextColor = Color(0xFFFF9500),
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

// 6. CAMERA SUB APP
@Composable
fun CameraSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    var shutterClicked by remember { mutableStateOf(false) }
    
    LaunchedEffect(shutterClicked) {
        if (shutterClicked) {
            delay(150)
            shutterClicked = false
        }
    }

    SimulatedAppContainer("Camera", viewModel, onClose) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Simulated Viewfinder Lens view
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f/4f)
                    .align(Alignment.Center)
                    .background(Color(0xFF1C1C1E)),
                contentAlignment = Alignment.Center
            ) {
                // Background grid lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Rule of thirds
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(w/3f, 0f), Offset(w/3f, h), strokeWidth = 1f)
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(2*w/3f, 0f), Offset(2*w/3f, h), strokeWidth = 1f)
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(0f, h/3f), Offset(w, h/3f), strokeWidth = 1f)
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(0f, 2*h/3f), Offset(w, 2*h/3f), strokeWidth = 1f)
                }

                Text(
                    text = "iPhone 17 Viewfinder lens\nTap Shutter below to capture",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                // Simulated camera shutter overlay (White flash blink effect)
                AnimatedVisibility(
                    visible = shutterClicked,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.White))
                }
            }
            
            // Top controllers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(imageVector = Icons.Default.FlashOn, contentDescription = "", tint = Color.Yellow)
                Icon(imageVector = Icons.Default.HdrOn, contentDescription = "", tint = Color.White)
                Icon(imageVector = Icons.Default.Camera, contentDescription = "", tint = Color.White)
            }
            
            // Bottom Controllers
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black)
                    .padding(bottom = 30.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "PHOTO", color = Color(0xFFFFCC00), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery circle thumbnail
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(45.dp).clip(CircleShape).background(Color(0xFFE5A93C)))
                    }
                    
                    // Shutter Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { shutterClicked = true }
                    )
                    
                    // Rotate switch
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C1C1E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = "Switch", tint = Color.White)
                    }
                }
            }
        }
    }
}

// 7. MAPS SUB APP
@Composable
fun MapsSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var searchText by remember { mutableStateOf("") }

    SimulatedAppContainer("Maps", viewModel, onClose) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Draw schematic vector map inside Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                // Land base color
                drawRect(if (isDark) Color(0xFF1E1E1E) else Color(0xFFEAEAEA))
                
                // Draw water body (Bay area)
                val bay = Path().apply {
                    moveTo(0f, h * 0.4f)
                    quadraticTo(w * 0.3f, h * 0.6f, w * 0.5f, h * 0.4f)
                    quadraticTo(w * 0.8f, h * 0.2f, w, h * 0.3f)
                    lineTo(w, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                drawPath(bay, if (isDark) Color(0xFF0F3254) else Color(0xFFA6C5E3))
                
                // Roads grids lines
                val roadColor = if (isDark) Color(0xFF333333) else Color.White
                val majorRoadColor = if (isDark) Color(0xFFFF9500) else Color(0xFFFFCC00)
                
                // Secondary grid roads
                for (i in 1..9) {
                    drawLine(roadColor, Offset(0f, h * i / 10f), Offset(w, h * i / 10f), strokeWidth = 2.dp.toPx())
                    drawLine(roadColor, Offset(w * i / 10f, 0f), Offset(w * i / 10f, h), strokeWidth = 2.dp.toPx())
                }
                
                // Highway loops
                drawLine(majorRoadColor, Offset(0f, h * 0.5f), Offset(w, h * 0.55f), strokeWidth = 6.dp.toPx())
                drawLine(majorRoadColor, Offset(w * 0.4f, 0f), Offset(w * 0.45f, h), strokeWidth = 6.dp.toPx())
                
                // Red Apple Pin Cupertino
                drawCircle(Color(0xFF007AFF), radius = 12.dp.toPx(), center = Offset(w * 0.42f, h * 0.52f))
                drawCircle(Color.White, radius = 4.dp.toPx(), center = Offset(w * 0.42f, h * 0.52f))
            }
            
            // Search toolbar panel
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextFieldSecure(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { searchText = "Cupertino, Infinite Loop" }) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = "", tint = Color(0xFF007AFF))
                    }
                }
            }
            
            // Floating center orientation button
            FloatingActionButton(
                onClick = { /* Locates user */ },
                containerColor = if (isDark) Color(0xFF2C2C2E) else Color.White,
                contentColor = Color(0xFF007AFF),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.MyLocation, contentDescription = "")
            }
        }
    }
}

// 8. APP STORE SUB APP
@Composable
fun AppStoreSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var activeStoreTab by remember { mutableStateOf(0) } // 0: Today, 1: Apps, 2: Search
    val storeCatalog = viewModel.storeApps
    val context = LocalContext.current

    SimulatedAppContainer("App Store", viewModel, onClose) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
                when (activeStoreTab) {
                    0 -> { // Today Feature Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(text = "FRIDAY, JUNE 19", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            Text(text = "Today", color = if (isDark) Color.White else Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                            
                            // Spotlight Hero Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(340.dp)
                                    .padding(bottom = 20.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Simulated App Store Gradient Art cover
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(Color(0xFFE0C3FC), Color(0xFF8EC5FC))
                                                )
                                            )
                                    )
                                    Column(modifier = Modifier.padding(20.dp).align(Alignment.TopStart)) {
                                        Text(text = "HOT FEATURED", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Unleash Social Creativity", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    
                                    // Float descriptive info
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = "Instagram", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(text = "Bring you closer to the people.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = {
                                                val app = storeCatalog.find { it.packageName == "com.instagram.android" }
                                                if (app != null && !app.isInstalled && app.installProgress == -1f) {
                                                    viewModel.installAppFromStore(app)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF007AFF)),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text("GET")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Apps Tab
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Apps", color = if (isDark) Color.White else Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                            
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(storeCatalog) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Colored App Logo Icon block
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(android.graphics.Color.parseColor(item.iconColorHex))),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            IOSIcon(name = item.materialIconName, modifier = Modifier.fillMaxSize())
                                        }
                                        
                                        Spacer(modifier = Modifier.width(16.dp))
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = item.label, color = if (isDark) Color.White else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            Text(text = item.category, color = Color.Gray, fontSize = 12.sp)
                                            Text(text = item.rating, color = Color(0xFFFF9500), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        
                                        // App State Install triggers
                                        if (item.isInstalled) {
                                            Button(
                                                onClick = {
                                                    Toast.makeText(context, "App already available on Launcher pages!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E5EA), contentColor = Color(0xFF007AFF)),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text("OPEN")
                                            }
                                        } else if (item.installProgress >= 0f) {
                                            // Downloading circle loader
                                            CircularProgressIndicator(
                                                progress = item.installProgress,
                                                color = Color(0xFF007AFF),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Button(
                                                onClick = { viewModel.installAppFromStore(item) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF), contentColor = Color.White),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text("GET")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Search Tab
                        Column {
                            Text(text = "Search", color = if (isDark) Color.White else Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                            var searchStoreText by remember { mutableStateOf("") }
                            
                            TextField(
                                value = searchStoreText,
                                onValueChange = { searchStoreText = it },
                                placeholder = { Text("App Store, Developers, Guides") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                                    unfocusedContainerColor = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Trending Suggestions", color = if (isDark) Color.White else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                            
                            val suggestions = listOf("WhatsApp Messenger", "Spotify podcasts", "TikTok videos", "Netflix streaming")
                            suggestions.forEach { suggest ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { searchStoreText = suggest }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "", tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = suggest, color = if (isDark) Color.White else Color.Black, fontSize = 15.sp)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.2f))
                            }
                        }
                    }
                }
            }
            
            // Bottom Tab navigation bar
            NavigationBar(containerColor = if (isDark) Color(0xFF161618) else Color(0xFFE5E5EA)) {
                NavigationBarItem(
                    selected = activeStoreTab == 0,
                    onClick = { activeStoreTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "") },
                    label = { Text("Today") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF007AFF), selectedTextColor = Color(0xFF007AFF), unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = activeStoreTab == 1,
                    onClick = { activeStoreTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.Apps, contentDescription = "") },
                    label = { Text("Apps") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF007AFF), selectedTextColor = Color(0xFF007AFF), unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = activeStoreTab == 2,
                    onClick = { activeStoreTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
                    label = { Text("Search") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF007AFF), selectedTextColor = Color(0xFF007AFF), unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                )
            }
        }
    }
}

// 9. SETTINGS SUB APP
@Composable
fun SettingsSubApp(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val isDark = viewModel.isDarkMode.collectAsState().value
    var insideWallpaperPicker by remember { mutableStateOf(false) }

    SimulatedAppContainer("Settings", viewModel, onClose) {
        if (!insideWallpaperPicker) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Settings Header title view
                Text(
                    text = "Settings",
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
                )
                
                // Profile header Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar placeholder details
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9500)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "R", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "A.R. Prince", color = if (isDark) Color.White else Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Apple ID, iCloud, Media & Purchases", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Settings List Categories
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                ) {
                    // Dark theme toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.DarkMode, contentDescription = "", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Dark Appearance", color = if (isDark) Color.White else Color.Black, fontSize = 16.sp)
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.toggleSetting("dark_mode", true) },
                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF34C759))
                        )
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.15f), modifier = Modifier.padding(start = 58.dp))
                    
                    // Wallpaper picker trigger row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { insideWallpaperPicker = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color(0xFF007AFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Wallpaper, contentDescription = "", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Wallpaper Settings", color = if (isDark) Color.White else Color.Black, fontSize = 16.sp)
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "", tint = Color.Gray)
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.15f), modifier = Modifier.padding(start = 58.dp))
                    
                    // Assistive physical touch trigger row
                    val assistiveTouch = viewModel.isAssistiveTouchOpen.collectAsState().value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color(0xFF34C759)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Accessibility, contentDescription = "", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Assistive Touch Panel", color = if (isDark) Color.White else Color.Black, fontSize = 16.sp)
                        }
                        Switch(
                            checked = assistiveTouch,
                            onCheckedChange = { viewModel.isAssistiveTouchOpen.value = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF34C759))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Device general specs
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                        .padding(16.dp)
                ) {
                    Text(text = "Device Information", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    
                    val specs = listOf(
                        "Model Name" to "iPhone 17 Pro Max (Simulated)",
                        "iOS Version" to "18.2",
                        "Chipname" to "Apple A19 Bionic",
                        "System Storage" to "1024 GB"
                    )
                    specs.forEach { spec ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = spec.first, color = Color.Gray, fontSize = 14.sp)
                            Text(text = spec.second, color = if (isDark) Color.White else Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        } else {
            // Wallpaper Selection layout page
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { insideWallpaperPicker = false }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(text = "Wallpapers", color = if (isDark) Color.White else Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                
                Text(text = "CHOOSE A WALLPAPER BACKGROUND", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Preloaded Vibrant Palette choice
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.selectWallpaper("vibrant")
                                insideWallpaperPicker = false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (viewModel.activeWallpaper.value == "vibrant") 3.dp else 0.dp,
                                    color = Color(0xFF007AFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF8EC5FC), Color(0xFFE0C3FC))
                                        )
                                    )
                            )
                        }
                        Text(text = "Vibrant", color = if (isDark) Color.White else Color.Black, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold)
                    }

                    // Preloaded generated Classic wallpaper card preview
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.selectWallpaper("classic")
                                insideWallpaperPicker = false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (viewModel.activeWallpaper.value == "classic") 3.dp else 0.dp,
                                    color = Color(0xFF007AFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFFF2D55), Color(0xFFFF9500), Color(0xFF5AC8FA))
                                        )
                                    )
                            )
                        }
                        Text(text = "Classic", color = if (isDark) Color.White else Color.Black, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold)
                    }
                    
                    // Preloaded generated Dark wallpaper card preview
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.selectWallpaper("dark")
                                insideWallpaperPicker = false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (viewModel.activeWallpaper.value == "dark") 3.dp else 0.dp,
                                    color = Color(0xFF007AFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF5856D6), Color(0xFF000000), Color(0xFF1A1A1A))
                                        )
                                    )
                            )
                        }
                        Text(text = "Indigo", color = if (isDark) Color.White else Color.Black, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
