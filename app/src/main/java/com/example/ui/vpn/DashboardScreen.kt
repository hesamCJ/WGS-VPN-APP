package com.example.ui.vpn

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppSettings
import com.example.ui.components.*
import com.example.ui.localization.LocaleHelper
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel
import java.util.*
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: VpnViewModel,
    settings: AppSettings,
    onNavigateToServers: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val downloadSpeed by viewModel.downloadSpeed.collectAsState()
    val uploadSpeed by viewModel.uploadSpeed.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val totalDownloadedBytes by viewModel.totalDownloadedBytes.collectAsState()

    val lang = settings.language
    val scrollState = rememberScrollState()

    // Mode-specific decoration color
    val modeAccent = when (settings.vpnMode) {
        "GAMING" -> NeonGreen
        "BYPASS" -> NeonOrange
        "GLOBAL" -> NeonPurple
        else -> NeonBlue
    }

    // Convert total traffic to formatted string
    val totalTrafficFormatted = remember(totalDownloadedBytes) {
        val mb = totalDownloadedBytes / (1024.0 * 1024.0)
        if (mb > 1024) {
            String.format(Locale.US, "%.2f GB", mb / 1024.0)
        } else {
            String.format(Locale.US, "%.1f MB", mb)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // 1. Sleek deep-space radial nebula glow near the top-center
                val nebulaBrush = Brush.radialGradient(
                    colors = listOf(
                        NeonBlue.copy(alpha = 0.08f),
                        NeonPurple.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height * 0.25f),
                    radius = size.width * 0.9f
                )
                drawRect(brush = nebulaBrush)

                // 2. Cosmic background stars (distinct coordinate constellations to feel like modern astrometrics)
                val timeSeed = System.currentTimeMillis()
                // Star 1
                val star1Alpha = (0.2f + 0.3f * sin(timeSeed / 450.0).toFloat()).coerceIn(0f, 1f)
                drawCircle(color = Color.White.copy(alpha = star1Alpha), radius = 2.5f, center = Offset(size.width * 0.12f, size.height * 0.15f))
                // Star 2
                val star2Alpha = (0.15f + 0.35f * sin(timeSeed / 600.0 + 1.2).toFloat()).coerceIn(0f, 1f)
                drawCircle(color = NeonBlue.copy(alpha = star2Alpha), radius = 3.5f, center = Offset(size.width * 0.88f, size.height * 0.10f))
                // Star 3
                val star3Alpha = (0.3f + 0.2f * sin(timeSeed / 350.0 + 2.5).toFloat()).coerceIn(0f, 1f)
                drawCircle(color = Color.White.copy(alpha = star3Alpha), radius = 2f, center = Offset(size.width * 0.25f, size.height * 0.45f))
                // Star 4
                val star4Alpha = (0.1f + 0.4f * sin(timeSeed / 500.0 + 0.5).toFloat()).coerceIn(0f, 1f)
                drawCircle(color = NeonGreen.copy(alpha = star4Alpha), radius = 3f, center = Offset(size.width * 0.80f, size.height * 0.65f))
                // Star 5
                val star5Alpha = (0.25f + 0.25f * sin(timeSeed / 800.0 + 3.1).toFloat()).coerceIn(0f, 1f)
                drawCircle(color = NeonPurple.copy(alpha = star5Alpha), radius = 3f, center = Offset(size.width * 0.15f, size.height * 0.85f))
                // Star 6
                val star6Alpha = (0.35f + 0.15f * sin(timeSeed / 300.0).toFloat()).coerceIn(0f, 1f)
                drawCircle(color = Color.White.copy(alpha = star6Alpha), radius = 2f, center = Offset(size.width * 0.70f, size.height * 0.22f))
            }
            .background(CosmicBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // Fast Header Toggle (Logo + Language selector)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NeonBlue, NeonGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "W",
                        color = CosmicBg,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "WGS_VPN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "v2box Core Engine",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // High-fidelity instant language switcher
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CosmicCard)
                    .clickable {
                        val nextLang = if (lang == "FA") "EN" else "FA"
                        viewModel.updateSettings(
                            language = nextLang,
                            mode = settings.vpnMode,
                            customDns = settings.customDns,
                            mtuOptimize = settings.enableMtuOptimization,
                            gamingMTU = settings.gamingMTU,
                            killSwitch = settings.killSwitch,
                            encryptionProtocol = settings.encryptionProtocol
                        )
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = NeonBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (lang == "FA") "English" else "فارسی",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Active State Mode Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, modeAccent.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(modeAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${LocaleHelper.getString("lbl_mode", lang)}: ",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = when (settings.vpnMode) {
                        "GAMING" -> LocaleHelper.getString("mode_gaming", lang)
                        "BYPASS" -> LocaleHelper.getString("mode_bypass", lang)
                        "GLOBAL" -> LocaleHelper.getString("mode_global", lang)
                        else -> LocaleHelper.getString("mode_normal", lang)
                    },
                    color = modeAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Glowing Core Handshake Connect Button
        Spacer(modifier = Modifier.height(10.dp))
        GlowingConnectButton(
            state = connectionState,
            onClick = { viewModel.toggleConnection() },
            lang = lang
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Currently selected Active Server Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToServers() }
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCardElevated)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    selectedServer?.let { server ->
                        CountryFlagBadge(countryCode = server.countryCode, size = 32.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = server.name,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${server.type} • ${server.host}:${server.port}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    } ?: run {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "No Server",
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocaleHelper.getString("lbl_empty_servers", lang),
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    selectedServer?.let { server ->
                        val pingColor = when {
                            server.ping in 0..60 -> NeonGreen
                            server.ping in 61..140 -> NeonBlue
                            server.ping > 140 -> NeonOrange
                            else -> TextSecondary
                        }
                        
                        Text(
                            text = if (server.ping >= 0) "${server.ping} ms" else "N/A",
                            color = pingColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(pingColor.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select Server",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Live Speed Canvas Graph (Visible when connected)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "FA") "نمودار زنده پکت‌های شبکه" else "Real-time Traffic Activity",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                RealtimeSpeedChart(
                    isConnected = connectionState == VpnViewModel.ConnectionState.CONNECTED,
                    downloadSpeed = downloadSpeed,
                    uploadSpeed = uploadSpeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Download/Upload speeds indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoTile(
                        title = LocaleHelper.getString("lbl_download", lang),
                        value = String.format(Locale.US, "%.1f KB/s", downloadSpeed),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Download",
                                tint = NeonBlue
                            )
                        },
                        accentColor = NeonBlue,
                        modifier = Modifier.weight(1f)
                    )

                    InfoTile(
                        title = LocaleHelper.getString("lbl_upload", lang),
                        value = String.format(Locale.US, "%.1f KB/s", uploadSpeed),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Upload",
                                tint = NeonGreen
                            )
                        },
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Technical details & Security Info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = LocaleHelper.getString("lbl_details", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "MTU Optimization Size", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = if (settings.enableMtuOptimization) "${settings.gamingMTU}" else "Auto-standard (1500)",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Custom Gateway DNS", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = settings.customDns.split(",").firstOrNull() ?: "Default",
                            color = NeonBlue,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (lang == "FA") "حجم ترافیک دریافت شده" else "Total Received Traffic", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = totalTrafficFormatted,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (lang == "FA") "پروتکل رمزنگاری فعال" else "Active Encryption Cipher", color = TextSecondary, fontSize = 12.sp)
                        val formattedProtocol = when (settings.encryptionProtocol) {
                            "AES_256_GCM" -> "AES-256-GCM AEAD"
                            "XCHACHA20_POLY1305" -> "XChaCha20-Poly1305 (192-bit)"
                            else -> "ChaCha20-Poly1305 AEAD"
                        }
                        Text(
                            text = formattedProtocol,
                            color = CyberPink,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = GridLine, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield Guard",
                        tint = NeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleHelper.getString("lbl_security_info", lang),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
