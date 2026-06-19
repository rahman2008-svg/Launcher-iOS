package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Locale

@Composable
fun IOSIcon(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp)) // iOS Squircle Curve
            .background(Color.White)
    ) {
        when (name) {
            "Safari" -> SafariIconDetails()
            "Weather" -> WeatherIconDetails()
            "Photos" -> PhotosIconDetails()
            "AppStore" -> AppStoreIconDetails()
            "Settings" -> SettingsIconDetails()
            "Camera" -> CameraIconDetails()
            "Maps" -> MapsIconDetails()
            "Calendar" -> CalendarIconDetails()
            "Clock" -> ClockIconDetails()
            "Health" -> HealthIconDetails()
            
            // App Store Extras Launcher Icons
            "chat" -> WhatsAppIconDetails()
            "photo_camera" -> InstagramIconDetails()
            "music_note" -> SpotifyIconDetails()
            "movie" -> NetflixIconDetails()
            "play_circle" -> YouTubeIconDetails()
            "theater_comedy" -> TikTokIconDetails()
            
            else -> {
                // Fallback icon based on name
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SafariIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        // Sky blue sea background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD)),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width * 0.7f
            )
        )
        
        // Circular white dial with compass ticks
        val outerRadius = size.width * 0.38f
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(
            color = Color.White,
            radius = outerRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Compass needle
        val compassPath = Path().apply {
            moveTo(center.x, center.y - outerRadius + 8.dp.toPx()) // North red tip
            lineTo(center.x + 8.dp.toPx(), center.y)
            lineTo(center.x - 8.dp.toPx(), center.y)
            close()
        }
        drawPath(compassPath, Color(0xFFFF3B30))
        
        val compassSouthPath = Path().apply {
            moveTo(center.x, center.y + outerRadius - 8.dp.toPx()) // South white tip
            lineTo(center.x + 8.dp.toPx(), center.y)
            lineTo(center.x - 8.dp.toPx(), center.y)
            close()
        }
        drawPath(compassSouthPath, Color(0xFFE5E5EA))
    }
}

@Composable
fun WeatherIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        
        // Sun-baked blue sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF56CCF2), Color(0xFF2F80ED))
            )
        )
        
        // Sun
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFEA79), Color(0xFFFF9F0A)),
                center = Offset(size.width * 0.35f, size.height * 0.38f),
                radius = size.width * 0.25f
            ),
            radius = size.width * 0.2f,
            center = Offset(size.width * 0.35f, size.height * 0.38f)
        )
        
        // Glassmorphic Cloud
        val cloudPath = Path().apply {
            val cx = size.width * 0.55f
            val cy = size.height * 0.65f
            val rw = size.width * 0.45f
            val rh = size.height * 0.25f
            addRoundRect(
                RoundRect(
                    left = cx - rw / 2,
                    top = cy - rh / 2,
                    right = cx + rw / 2,
                    bottom = cy + rh / 2,
                    cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                )
            )
        }
        drawPath(cloudPath, Color(0xE0FFFFFF))
        
        drawCircle(
            color = Color(0xE0FFFFFF),
            radius = size.width * 0.18f,
            center = Offset(size.width * 0.5f, size.height * 0.55f)
        )
        drawCircle(
            color = Color(0xE0FFFFFF),
            radius = size.width * 0.14f,
            center = Offset(size.width * 0.68f, size.height * 0.6f)
        )
    }
}

@Composable
fun PhotosIconDetails() {
    // Pure white with 8 colored petals
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
            val size = this.size
            val center = Offset(size.width / 2, size.height / 2)
            val petalRadius = size.width * 0.16f
            
            val colors = listOf(
                Color(0xFFFF3B30), // Top red
                Color(0xFFFF9F0A), // Orange
                Color(0xFFFFD60A), // Yellow
                Color(0xFF34C759), // Green
                Color(0xFF64D2FF), // Cyan
                Color(0xFF0A84FF), // Blue
                Color(0xFFBF5AF2), // Purple
                Color(0xFFFF6484)  // Pink
            )
            
            colors.forEachIndexed { i, col ->
                val angleRad = Math.toRadians((i * 45).toDouble())
                val offsetDist = size.width * 0.2f
                val petalCenter = Offset(
                    x = (center.x + offsetDist * Math.cos(angleRad)).toFloat(),
                    y = (center.y + offsetDist * Math.sin(angleRad)).toFloat()
                )
                
                // Draw translucent petals
                drawCircle(
                    color = col.copy(alpha = 0.85f),
                    radius = petalRadius,
                    center = petalCenter
                )
            }
        }
    }
}

@Composable
fun AppStoreIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        
        // Azure blue gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1D976C), Color(0xFF93F9B9).copy(alpha = 0f)) // Wait, azure blue:
            )
        )
        // Clean azure gradient override
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
            )
        )
        
        val center = Offset(size.width / 2, size.height / 2)
        val strokeWidth = 8.dp.toPx()
        val barLength = size.width * 0.55f
        
        // Procedural white "A" layout bars
        // Intersecting structural sticks
        val shadowPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            setShadowLayer(8f, 0f, 4f, Color.Black.copy(alpha = 0.25f).toArgb())
        }
        
        // Left diagonal stick
        val angle1Rad = Math.toRadians(60.0)
        drawContext.canvas.nativeCanvas.save()
        drawContext.canvas.nativeCanvas.translate(center.x, center.y)
        
        // Left stick
        drawLine(
            color = Color.White,
            start = Offset(-barLength / 2 * Math.cos(angle1Rad).toFloat(), barLength / 3),
            end = Offset(barLength / 2 * Math.cos(angle1Rad).toFloat(), -barLength / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Right stick
        drawLine(
            color = Color.White,
            start = Offset(barLength / 2 * Math.cos(angle1Rad).toFloat(), barLength / 3),
            end = Offset(-barLength / 2 * Math.cos(angle1Rad).toFloat(), -barLength / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Horizontal cross stick
        drawLine(
            color = Color.White,
            start = Offset(-barLength / 2.3f, 0f),
            end = Offset(barLength / 2.3f, 0f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        drawContext.canvas.nativeCanvas.restore()
    }
}

@Composable
fun SettingsIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        val center = Offset(size.width / 2, size.height / 2)
        
        // Silver gray metallic mesh background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE5E5EA), Color(0xFF8E8E93))
            )
        )
        
        // Outer cog circles
        drawCircle(
            color = Color(0xFFA1A1A5),
            radius = size.width * 0.35f,
            center = center
        )
        
        drawCircle(
            color = Color(0xFFC7C7CC),
            radius = size.width * 0.30f,
            center = center
        )
        
        // Draw gears teeth procedurally
        for (i in 0..11) {
            val angleRad = Math.toRadians((i * 30).toDouble())
            val innerPoint = Offset(
                x = (center.x + size.width * 0.28f * Math.cos(angleRad)).toFloat(),
                y = (center.y + size.height * 0.28f * Math.sin(angleRad)).toFloat()
            )
            val outerPoint = Offset(
                x = (center.x + size.width * 0.38f * Math.cos(angleRad)).toFloat(),
                y = (center.y + size.height * 0.38f * Math.sin(angleRad)).toFloat()
            )
            drawLine(
                color = Color(0xFFA1A1A5),
                start = innerPoint,
                end = outerPoint,
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        
        // Core hole
        drawCircle(
            color = Color(0xFFE5E5EA),
            radius = size.width * 0.12f,
            center = center
        )
        drawCircle(
            color = Color(0xFF5E5E62),
            radius = size.width * 0.08f,
            center = center
        )
    }
}

@Composable
fun CameraIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        // Silver-metal camera body
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE5E5EA), Color(0xFF8E8E93))
            )
        )
        
        val center = Offset(size.width / 2, size.height / 2)
        
        // Camera outer lens circle
        drawCircle(
            color = Color(0xFF3A3A3C),
            radius = size.width * 0.34f,
            center = center
        )
        
        // Lens glass
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1C1C1E), Color(0xFF000000)),
                center = center,
                radius = size.width * 0.28f
            ),
            radius = size.width * 0.28f,
            center = center
        )
        
        // Cyan and yellow lens reflections
        drawArc(
            color = Color(0xFF5AC8FA).copy(alpha = 0.5f),
            startAngle = 210f,
            sweepAngle = 60f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx()),
            size = Size(size.width * 0.44f, size.width * 0.44f),
            topLeft = Offset(center.x - size.width * 0.22f, center.y - size.width * 0.22f)
        )
        
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = size.width * 0.04f,
            center = Offset(center.x + size.width * 0.1f, center.y - size.width * 0.1f)
        )
    }
}

@Composable
fun MapsIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        
        // Background grass/roads colors
        drawRect(Color(0xFFE4F2E7)) // Pastel yellow-green
        
        // Draw diagonal road grids
        val gridLineColor = Color(0xFFF2F2F7)
        drawLine(
            color = gridLineColor,
            start = Offset(0f, size.height * 0.3f),
            end = Offset(size.width, size.height * 0.3f),
            strokeWidth = 6.dp.toPx()
        )
        drawLine(
            color = gridLineColor,
            start = Offset(0f, size.height * 0.7f),
            end = Offset(size.width, size.height * 0.7f),
            strokeWidth = 8.dp.toPx()
        )
        drawLine(
            color = gridLineColor,
            start = Offset(size.width * 0.5f, 0f),
            end = Offset(size.width * 0.5f, size.height),
            strokeWidth = 10.dp.toPx()
        )
        
        // Curved blue highway path
        val routePath = Path().apply {
            moveTo(0f, size.height * 0.1f)
            quadraticTo(
                size.width * 0.4f, size.height * 0.4f,
                size.width * 0.6f, size.height * 0.9f
            )
        }
        drawPath(
            path = routePath,
            color = Color(0xFF007AFF),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // iOS Navigation Pin
        val pinCenter = Offset(size.width * 0.58f, size.height * 0.65f)
        drawCircle(
            color = Color(0xFFFF2D55),
            radius = size.width * 0.08f,
            center = pinCenter
        )
        
        val pinCone = Path().apply {
            moveTo(pinCenter.x - size.width * 0.08f, pinCenter.y)
            lineTo(pinCenter.x + size.width * 0.08f, pinCenter.y)
            lineTo(pinCenter.x, pinCenter.y + size.width * 0.18f)
            close()
        }
        drawPath(pinCone, Color(0xFFFF2D55))
        
        // Pin white core dot
        drawCircle(
            color = Color.White,
            radius = size.width * 0.03f,
            center = pinCenter
        )
    }
}

@Composable
fun CalendarIconDetails() {
    // Current systems month/day names
    val calendar = Calendar.getInstance()
    val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())?.uppercase() ?: "JUN"
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "Friday"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Red Top ribbon header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.32f)
                .background(Color(0xFFFF3B30)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = monthName,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
        
        // White bottom showing values
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.68f)
                .padding(bottom = 2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfMonth,
                color = Color(0xFF1C1C1E),
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 24.sp
            )
            Text(
                text = dayOfWeek.take(3).uppercase(),
                color = Color(0xFF8E8E93),
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ClockIconDetails() {
    val currentTime = remember { mutableStateOf(Calendar.getInstance()) }
    
    // Auto tick ticking sweep logic
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = Calendar.getInstance()
            kotlinx.coroutines.delay(1000)
        }
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        val center = Offset(size.width / 2, size.height / 2)
        
        // Clock black metal plate
        drawCircle(
            color = Color(0xFF1C1C1E),
            radius = size.width * 0.48f,
            center = center
        )
        
        // Delicate white dial graduation markings
        for (i in 0..11) {
            val angleRad = Math.toRadians((i * 30).toDouble())
            val dotCenter = Offset(
                x = (center.x + size.width * 0.40f * Math.cos(angleRad)).toFloat(),
                y = (center.y + size.height * 0.40f * Math.sin(angleRad)).toFloat()
            )
            drawCircle(
                color = Color(0xFFE5E5EA),
                radius = 1.2.dp.toPx(),
                center = dotCenter
            )
        }
        
        val hour = currentTime.value.get(Calendar.HOUR)
        val minute = currentTime.value.get(Calendar.MINUTE)
        val second = currentTime.value.get(Calendar.SECOND)
        
        // Hours hand mapping
        val hourAngleRad = Math.toRadians(((hour * 30) + (minute * 0.5) - 90).toDouble())
        val hourLength = size.width * 0.22f
        drawLine(
            color = Color.White,
            start = center,
            end = Offset(
                (center.x + hourLength * Math.cos(hourAngleRad)).toFloat(),
                (center.y + hourLength * Math.sin(hourAngleRad)).toFloat()
            ),
            strokeWidth = 3.2.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Minutes hand mapping
        val minAngleRad = Math.toRadians(((minute * 6) - 90).toDouble())
        val minLength = size.width * 0.33f
        drawLine(
            color = Color.White,
            start = center,
            end = Offset(
                (center.x + minLength * Math.cos(minAngleRad)).toFloat(),
                (center.y + minLength * Math.sin(minAngleRad)).toFloat()
            ),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Sweeping Orange second hand
        val secAngleRad = Math.toRadians(((second * 6) - 90).toDouble())
        val secLength = size.width * 0.38f
        drawLine(
            color = Color(0xFFFF9500),
            start = center,
            end = Offset(
                (center.x + secLength * Math.cos(secAngleRad)).toFloat(),
                (center.y + secLength * Math.sin(secAngleRad)).toFloat()
            ),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Cap center pivot pin
        drawCircle(
            color = Color(0xFFFF9500),
            radius = 2.dp.toPx(),
            center = center
        )
    }
}

@Composable
fun HealthIconDetails() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = this.size
        drawRect(Color.White)
        
        val center = Offset(size.width / 2, size.height / 2)
        val r = size.width * 0.22f
        
        // Heart drawing logic
        val heartPath = Path().apply {
            moveTo(center.x, center.y + r * 0.4f)
            
            // Left lobe
            cubicTo(
                center.x - r, center.y - r,
                center.x - 2 * r, center.y + r * 0.1f,
                center.x, center.y + 1.6f * r
            )
            // Right lobe
            cubicTo(
                center.x + 2 * r, center.y + r * 0.1f,
                center.x + r, center.y - r,
                center.x, center.y + r * 0.4f
            )
        }
        
        drawPath(
            path = heartPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFF3B30), Color(0xFFFF2D55))
            )
        )
    }
}

@Composable
fun WhatsAppIconDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF25D366)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "WhatsApp",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
fun InstagramIconDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF833AB4), // Purple
                        Color(0xFFFD1D1D), // Red
                        Color(0xFFFCAF45)  // Yellow-orange
                    ),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Instagram",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun SpotifyIconDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF191414)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val size = this.size
            val center = Offset(size.width / 2, size.height / 2)
            
            // Draw circle background
            drawCircle(Color(0xFF1DB954), radius = size.width / 2, center = center)
            
            // Minimal sound arcs
            drawArc(
                color = Color.Black,
                startAngle = -150f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width * 0.7f, size.width * 0.7f),
                topLeft = Offset(center.x - size.width * 0.35f, center.y - size.width * 0.35f)
            )
            drawArc(
                color = Color.Black,
                startAngle = -150f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width * 0.48f, size.width * 0.48f),
                topLeft = Offset(center.x - size.width * 0.24f, center.y - size.width * 0.24f)
            )
        }
    }
}

@Composable
fun NetflixIconDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "N",
            color = Color(0xFFE50914),
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun YouTubeIconDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFF0000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TikTokIconDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "TikTok",
            tint = Color(0xFF00F2FE),
            modifier = Modifier.size(34.dp)
        )
    }
}
