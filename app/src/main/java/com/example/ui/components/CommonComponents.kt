package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun GlowingConnectButton(
    state: VpnViewModel.ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    lang: String = "FA"
) {
    // Determine target pulse color depending on connection state
    val pulseColor = when (state) {
        VpnViewModel.ConnectionState.CONNECTED -> NeonGreen
        VpnViewModel.ConnectionState.CONNECTING -> NeonOrange
        VpnViewModel.ConnectionState.DISCONNECTING -> CyberPink
        VpnViewModel.ConnectionState.DISCONNECTED -> NeonBlue
    }

    val stateMultiplier = when (state) {
        VpnViewModel.ConnectionState.CONNECTED -> 3f
        VpnViewModel.ConnectionState.CONNECTING -> 6f
        VpnViewModel.ConnectionState.DISCONNECTING -> 4f
        VpnViewModel.ConnectionState.DISCONNECTED -> 1f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
    
    // Smooth breathing circle scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400 / stateMultiplier.toInt().coerceAtLeast(1), easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )

    // Fade out alpha for external ring
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400 / stateMultiplier.toInt().coerceAtLeast(1), easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    // Cosmic Rotation 1 (Clockwise telemetry dial)
    val angleClockwise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween((6000 / stateMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle_cw"
    )

    // Cosmic Rotation 2 (Counter-Clockwise compass ring)
    val angleCounterClockwise by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween((10000 / stateMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle_ccw"
    )

    // Reactor core organic breathing scale
    val coreBreatheScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = SineCupEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_breathe"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(220.dp)
            .clickable(onClick = onClick)
    ) {
        // High fidelity Canvas - Scifi Telemetry and Starry Radar Dashboard
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerOffset = center
            val baseRadius = size.minDimension / 2.2f

            // 1. Futuristic space background gas glow
            drawCircle(
                color = pulseColor.copy(alpha = 0.04f),
                radius = baseRadius * 1.15f,
                center = centerOffset
            )

            // 2. Twin cosmic star coordinates / Space telemetry dots
            val starAlpha = 0.4f * (sin(System.currentTimeMillis() / 400.0) * 0.3 + 0.7).toFloat()
            drawCircle(color = Color.White.copy(alpha = starAlpha), radius = 2f, center = Offset(width * 0.15f, height * 0.25f))
            drawCircle(color = NeonBlue.copy(alpha = starAlpha), radius = 3f, center = Offset(width * 0.85f, height * 0.35f))
            drawCircle(color = NeonPurple.copy(alpha = starAlpha * 0.7f), radius = 2.5f, center = Offset(width * 0.22f, height * 0.78f))
            drawCircle(color = NeonGreen.copy(alpha = starAlpha), radius = 2f, center = Offset(width * 0.76f, height * 0.82f))

            // 3. Glowing pulsating expanding shockwaves
            drawCircle(
                color = pulseColor.copy(alpha = pulseAlpha),
                radius = baseRadius * pulseScale,
                center = centerOffset
            )

            // 4. Outer Telemetry Dial - Dashed Rotating Star-Map Orbit
            drawCircle(
                color = pulseColor.copy(alpha = 0.2f),
                radius = baseRadius * 0.95f,
                center = centerOffset,
                style = Stroke(
                    width = 2f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(15f, 25f),
                        angleClockwise * 1.5f
                    )
                )
            )

            // 5. Opposite Spinning Compass Telemetry ring with Tech Ticks
            rotate(angleCounterClockwise) {
                // Compass major markers representing cardinal coordinate channels
                for (angle in 0 until 360 step 30) {
                    val isMajor = angle % 90 == 0
                    val length = if (isMajor) 15f else 7f
                    val strokeWidth = if (isMajor) 4f else 2f
                    val markerColor = if (isMajor) pulseColor else TextSecondary.copy(alpha = 0.4f)
                    
                    val angleRad = Math.toRadians(angle.toDouble())
                    val startRadius = baseRadius * 0.84f
                    val endRadius = startRadius + length
                    
                    val startX = centerOffset.x + (startRadius * Math.cos(angleRad)).toFloat()
                    val startY = centerOffset.y + (startRadius * Math.sin(angleRad)).toFloat()
                    val endX = centerOffset.x + (endRadius * Math.cos(angleRad)).toFloat()
                    val endY = centerOffset.y + (endRadius * Math.sin(angleRad)).toFloat()
                    
                    drawLine(
                        color = markerColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeWidth
                    )
                }

                // Precision telemetry orbit rings
                drawCircle(
                    color = pulseColor.copy(alpha = 0.15f),
                    radius = baseRadius * 0.84f,
                    center = centerOffset,
                    style = Stroke(width = 1f)
                )
            }

            // 6. Direct Sweep Radar Line (Only Active when connected)
            if (state == VpnViewModel.ConnectionState.CONNECTED || state == VpnViewModel.ConnectionState.CONNECTING) {
                val sweepAngleRad = Math.toRadians((angleClockwise * 1.2f).toDouble())
                val endX = centerOffset.x + (baseRadius * 0.84f * Math.cos(sweepAngleRad)).toFloat()
                val endY = centerOffset.y + (baseRadius * 0.84f * Math.sin(sweepAngleRad)).toFloat()
                
                drawLine(
                    color = pulseColor.copy(alpha = 0.4f),
                    start = centerOffset,
                    end = Offset(endX, endY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Mid semi-transparent static ring holding the central space engine capsule
        Box(
            modifier = Modifier
                .size(150.dp)
                .border(2.dp, pulseColor.copy(alpha = 0.25f), CircleShape)
                .padding(6.dp)
        ) {
            // Highly immersive dynamic reactor core capsule
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = coreBreatheScale,
                        scaleY = coreBreatheScale
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CosmicCard,
                                CosmicBg
                            )
                        )
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                pulseColor,
                                pulseColor.copy(alpha = 0.3f),
                                pulseColor
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 200f)
                        ),
                        shape = CircleShape
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Floating sci-fi crosshair decoration
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = pulseColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(24.dp)
                            .padding(bottom = 2.dp)
                    )

                    Text(
                        text = when (state) {
                            VpnViewModel.ConnectionState.CONNECTED -> if (lang == "FA") "خاموش کن" else "DISCONNECT"
                            VpnViewModel.ConnectionState.DISCONNECTED -> if (lang == "FA") "روشن کن" else "CONNECT"
                            else -> if (lang == "FA") "..." else "WAIT..."
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = pulseColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = if (lang == "FA") 0.sp else 2.sp,
                            fontSize = if (lang == "FA") 16.sp else 12.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = when (state) {
                            VpnViewModel.ConnectionState.CONNECTED -> if (lang == "FA") "کور فعال" else "STATE: ACTIVE"
                            VpnViewModel.ConnectionState.DISCONNECTED -> if (lang == "FA") "غیرفعال" else "STATE: SECURE"
                            VpnViewModel.ConnectionState.CONNECTING -> if (lang == "FA") "اتصال..." else "TUNNELING"
                            VpnViewModel.ConnectionState.DISCONNECTING -> if (lang == "FA") "قطع..." else "TERMINATING"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

// Custom Easing Helper
val SineCupEasing = Easing { fraction ->
    sin(fraction * Math.PI / 2f).toFloat()
}

@Composable
fun CountryFlagBadge(
    countryCode: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    val emoji = when (countryCode.uppercase()) {
        "DE" -> "🇩🇪"
        "FI" -> "🇫🇮"
        "SG" -> "🇸🇬"
        "US" -> "🇺🇸"
        "JP" -> "🇯🇵"
        "GB" -> "🇬🇧"
        "NL" -> "🇳🇱"
        "TR" -> "🇹🇷"
        "AE" -> "🇦🇪"
        "IR" -> "🇮🇷"
        else -> "🌐"
    }
    Text(
        text = emoji,
        fontSize = size.value.sp,
        modifier = modifier
    )
}

@Composable
fun RealtimeSpeedChart(
    isConnected: Boolean,
    downloadSpeed: Double,
    uploadSpeed: Double,
    modifier: Modifier = Modifier
) {
    var points by remember { mutableStateOf(FloatArray(40) { 0.1f }) }

    // Keep adding live data points matching speed fluctuation
    LaunchedEffect(isConnected, downloadSpeed) {
        if (isConnected) {
            while (true) {
                val percentageValue = (downloadSpeed / 1200.0).toFloat().coerceIn(0.1f, 0.9f)
                val newPoints = points.copyOf()
                System.arraycopy(newPoints, 1, newPoints, 0, newPoints.size - 1)
                newPoints[newPoints.size - 1] = (percentageValue + (sin(System.currentTimeMillis() / 1500f) * 0.1f).toFloat()).coerceIn(0.1f, 0.9f)
                points = newPoints
                delay(300)
            }
        } else {
            while (true) {
                if (points.any { it > 0.12f }) {
                    // Smooth decay to baseline when turned off
                    val newPoints = points.copyOf()
                    for (i in newPoints.indices) {
                        newPoints[i] = (newPoints[i] * 0.7f).coerceIn(0.1f, 1.0f)
                    }
                    points = newPoints
                }
                delay(300)
            }
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val pointsSize = points.size
        if (pointsSize < 2) return@Canvas
        
        val step = width / (pointsSize - 1)
        
        val linePath = Path()
        val fillPath = Path()
        
        linePath.moveTo(0f, height - (points[0] * height))
        fillPath.moveTo(0f, height)
        fillPath.lineTo(0f, height - (points[0] * height))

        for (i in 1 until pointsSize) {
            val currentX = i * step
            val currentY = height - (points[i] * height)
            
            // smooth curves using cubic control points
            val prevX = (i - 1) * step
            val prevY = height - (points[i - 1] * height)
            val cpX1 = prevX + (step / 2f)
            val cpY1 = prevY
            val cpX2 = prevX + (step / 2f)
            val cpY2 = currentY
            
            linePath.cubicTo(cpX1, cpY1, cpX2, cpY2, currentX, currentY)
            fillPath.cubicTo(cpX1, cpY1, cpX2, cpY2, currentX, currentY)
        }
        
        fillPath.lineTo(width, height)
        fillPath.close()

        // Draw glowing translucent gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    NeonBlue.copy(alpha = 0.25f),
                    Color.Transparent
                )
            )
        )

        // Draw the neon speed curve path line
        drawPath(
            path = linePath,
            color = NeonBlue,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw animated live scanner pin at the leading edge
        if (isConnected && pointsSize > 0) {
            val lastX = width
            val lastY = height - (points[pointsSize - 1] * height)
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(lastX, lastY)
            )
            drawCircle(
                color = NeonBlue,
                radius = 11.dp.toPx(),
                center = Offset(lastX, lastY),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
fun InfoTile(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun MetricBar(
    label: String,
    valueString: String,
    progress: Float,
    barColor: Color,
    lang: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = valueString,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = CosmicCardElevated,
        )
    }
}
