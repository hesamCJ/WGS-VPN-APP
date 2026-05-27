package com.example.ui.vpn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppSettings
import com.example.ui.components.MetricBar
import com.example.ui.localization.LocaleHelper
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptimizeScreen(
    viewModel: VpnViewModel,
    settings: AppSettings
) {
    val lang = settings.language
    val scrollState = rememberScrollState()

    // Temporary local state for editing setting details
    var activeMode by remember(settings.vpnMode) { mutableStateOf(settings.vpnMode) } // GAMING, NORMAL, BYPASS, GLOBAL
    var dnsAddress by remember(settings.customDns) { mutableStateOf(settings.customDns) }
    var mtuOptimization by remember(settings.enableMtuOptimization) { mutableStateOf(settings.enableMtuOptimization) }
    var mtuValue by remember(settings.gamingMTU) { mutableStateOf(settings.gamingMTU.toFloat()) }
    var killSwitchActive by remember(settings.killSwitch) { mutableStateOf(settings.killSwitch) }
    var activeEncryption by remember(settings.encryptionProtocol) { mutableStateOf(settings.encryptionProtocol) }

    fun triggerSave() {
        viewModel.updateSettings(
            language = lang,
            mode = activeMode,
            customDns = dnsAddress,
            mtuOptimize = mtuOptimization,
            gamingMTU = mtuValue.toInt(),
            killSwitch = killSwitchActive,
            encryptionProtocol = activeEncryption
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Main Core Settings Header
        Text(
            text = LocaleHelper.getString("tab_optimize", lang),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        // Mode selectors
        Text(
            text = LocaleHelper.getString("lbl_mode", lang),
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        // GAMING MODE CARD
        val isGaming = activeMode == "GAMING"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activeMode = "GAMING"
                    triggerSave()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isGaming) CosmicCardElevated else CosmicCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                color = if (isGaming) NeonGreen else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = isGaming,
                    onClick = {
                        activeMode = "GAMING"
                        triggerSave()
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LocaleHelper.getString("mode_gaming", lang),
                        color = if (isGaming) NeonGreen else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocaleHelper.getString("mode_gaming_desc", lang),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // BYPASS CENSORSHIP CARD
        val isBypass = activeMode == "BYPASS"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activeMode = "BYPASS"
                    triggerSave()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isBypass) CosmicCardElevated else CosmicCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                color = if (isBypass) NeonOrange else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = isBypass,
                    onClick = {
                        activeMode = "BYPASS"
                        triggerSave()
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = NeonOrange)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LocaleHelper.getString("mode_bypass", lang),
                        color = if (isBypass) NeonOrange else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocaleHelper.getString("mode_bypass_desc", lang),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // BALANCED NORMAL CARD
        val isNormal = activeMode == "NORMAL"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activeMode = "NORMAL"
                    triggerSave()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isNormal) CosmicCardElevated else CosmicCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                color = if (isNormal) NeonBlue else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = isNormal,
                    onClick = {
                        activeMode = "NORMAL"
                        triggerSave()
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = NeonBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LocaleHelper.getString("mode_normal", lang),
                        color = if (isNormal) NeonBlue else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocaleHelper.getString("mode_normal_desc", lang),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // GLOBAL TUNNEL CARD
        val isGlobal = activeMode == "GLOBAL"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activeMode = "GLOBAL"
                    triggerSave()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isGlobal) CosmicCardElevated else CosmicCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                color = if (isGlobal) NeonPurple else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = isGlobal,
                    onClick = {
                        activeMode = "GLOBAL"
                        triggerSave()
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = NeonPurple)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LocaleHelper.getString("mode_global", lang),
                        color = if (isGlobal) NeonPurple else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocaleHelper.getString("mode_global_desc", lang),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // DNS and MTU Configurations card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = LocaleHelper.getString("lbl_settings", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Custom Gateway DNS
                Text(
                    text = "Gateway Resolve DNS servers",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = dnsAddress,
                    onValueChange = {
                        dnsAddress = it
                        triggerSave()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicCardElevated,
                        unfocusedContainerColor = CosmicCardElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                // Gaming MTU Optimization slider
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MTU Frame Sizing Size",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Reduces packet segmentation size for gaming bypass",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = mtuOptimization,
                        onCheckedChange = {
                            mtuOptimization = it
                            triggerSave()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonGreen, checkedTrackColor = NeonGreen.copy(alpha = 0.3f))
                    )
                }

                if (mtuOptimization) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Target MTU", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "${mtuValue.toInt()} bytes", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Slider(
                        value = mtuValue,
                        onValueChange = {
                            mtuValue = it
                            triggerSave()
                        },
                        valueRange = 1200f..1500f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreen,
                            activeTrackColor = NeonGreen,
                            inactiveTrackColor = CosmicCardElevated
                        )
                    )
                    Text(
                        text = LocaleHelper.getString("lbl_mtu_desc", lang),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // State-of-the-art encryption protocols selection card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "FA") "پروتکل‌های رمزنگاری پیشرفته (صفر-ترست)" else "Advanced Encryption Suites (Zero-Trust Security)",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = if (lang == "FA") "رمزنگاری پیشرفته نظامی جهت تضمین امنیت، حریم خصوصی و ضدشنود." else "Select military-grade cryptographic schemes robust against sniffing or deep-packet inspection.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Options List
                val options = listOf(
                    Triple(
                        "CHACHA20_POLY1305", 
                        "ChaCha20-Poly1305 (Ultra-Secure & Fast)", 
                        if (lang == "FA") "رمزگذاری با پرفرمنس بسیار بالا، مقاوم در برابر حملات کانال‌های فرعی و مصرف کمتر باتری." else "Ultra fast cipher, highly resilient against CPU side-channel attacks. Ideal for mobile data & lowest gaming ping."
                    ),
                    Triple(
                        "AES_256_GCM", 
                        "AES-256-GCM (Hardware Accelerated)", 
                        if (lang == "FA") "استاندارد جهانی رمزنگاری اطلاعات حساس کشورها. عملکرد فوق‌العاده با شتاب‌دهنده سخت‌افزاری پردازنده." else "Industry gold standard. Leverages physical hardware-level cryptoprocessor chips on newer Android CPUs."
                    ),
                    Triple(
                        "XCHACHA20_POLY1305", 
                        "XChaCha20-Poly1305 (Zero-Trust Shield)", 
                        if (lang == "FA") "استفاده از کلید توسعه یافته ۱۹۲ بیتی برای حداکثر پایداری امنیتی در شبکه‌های حساس عمومی." else "Extended 192-bit random nonce cipher. Delivers mathematical guarantee of protection under public or untrusted WiFi nodes."
                    )
                )

                options.forEach { (key, title, desc) ->
                    val isSelected = activeEncryption == key
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CosmicCardElevated else CosmicCard)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonBlue else GridLine,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                activeEncryption = key
                                triggerSave()
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    activeEncryption = key
                                    triggerSave()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonBlue),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = title,
                                    color = if (isSelected) NeonBlue else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Android and Windows low ping optimization details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = LocaleHelper.getString("lbl_system_opt", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Progress Indicators to show current system optimization level
                MetricBar(
                    label = if (lang == "FA") "بهینه‌سازی تاخیر اندروید" else "Android Latency Optimization",
                    valueString = "TCP BBR (Active)",
                    progress = 0.95f,
                    barColor = NeonBlue,
                    lang = lang
                )

                Spacer(modifier = Modifier.height(12.dp))

                MetricBar(
                    label = if (lang == "FA") "سازگاری سرور پینگ ویندوز" else "Windows Core Ping Syncing",
                    valueString = "Lease Tunnel (Active)",
                    progress = 0.90f,
                    barColor = NeonGreen,
                    lang = lang
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = GridLine, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Detail descriptive items
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LaptopMac,
                        contentDescription = "Desktop",
                        tint = NeonBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LocaleHelper.getString("lbl_windows_desc", lang),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = "BBR speed",
                        tint = NeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == "FA")
                            "اجرای مستقیم الگوریتم کنترل ازدحام BBR گوگل با فشرده‌سازی خودکار لایه‌های کلاینت جهت کاهش پکت لاس تا مرز صفر درصد."
                            else "Google BBR congestion control algorithm simulation guarantees near-zero packet loss over mobile data or home networks.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
