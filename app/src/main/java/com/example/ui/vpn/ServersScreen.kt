package com.example.ui.vpn

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppSettings
import com.example.data.models.VpnServer
import com.example.ui.components.CountryFlagBadge
import com.example.ui.localization.LocaleHelper
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersScreen(
    viewModel: VpnViewModel,
    settings: AppSettings
) {
    val servers by viewModel.allServers.collectAsState()
    val isTestingPings by viewModel.isTestingPings.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()

    val lang = settings.language
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var showAddServerDialog by remember { mutableStateOf(false) }

    // Dialog state fields
    var serverName by remember { mutableStateOf("") }
    var serverType by remember { mutableStateOf("VLess") } // VLess, VMess, Shadowsocks, Trojan, WireGuard
    var hostAddress by remember { mutableStateOf("") }
    var portNumber by remember { mutableStateOf("443") }
    var credentialValue by remember { mutableStateOf("") }
    var serverCountry by remember { mutableStateOf("DE") }
    var sniHeader by remember { mutableStateOf("") }
    var wsPath by remember { mutableStateOf("") }
    var securityProtocol by remember { mutableStateOf("tls") }

    val filteredServers = remember(servers, searchQuery) {
        if (searchQuery.isBlank()) {
            servers
        } else {
            servers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.host.contains(searchQuery, ignoreCase = true) ||
                        it.type.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Fast Action Buttons row (Import Clipboard & Manual Add)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Import from Clipboard button
            Button(
                onClick = {
                    val clipText = clipboardManager.getText()?.text
                    if (clipText != null) {
                        viewModel.importFromClipboard(clipText)
                    } else {
                        viewModel.importFromClipboard("")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicCardElevated),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste Clipboard Config",
                    tint = NeonBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = LocaleHelper.getString("btn_import_clipboard", lang),
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Create button
            Button(
                onClick = { showAddServerDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = "Add custom profile",
                    tint = CosmicBg,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = LocaleHelper.getString("btn_add_server", lang),
                    color = CosmicBg,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Search Bar & Latency Engine Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = LocaleHelper.getString("lbl_search", lang),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CosmicCard,
                    unfocusedContainerColor = CosmicCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true
            )

            // Test Ping triggering button
            IconButton(
                onClick = { viewModel.testAllPings() },
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isTestingPings) CosmicCardElevated else CosmicCard)
            ) {
                if (isTestingPings) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = NeonGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Latency speed check",
                        tint = NeonGreen
                    )
                }
            }
        }

        // Configuration listings (LazyColumn)
        if (filteredServers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "No configs",
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = LocaleHelper.getString("lbl_empty_servers", lang),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredServers, key = { it.id }) { server ->
                    val isSelected = selectedServer?.id == server.id
                    
                    val borderBrush = if (isSelected) {
                        Brush.linearGradient(colors = listOf(NeonBlue, NeonGreen))
                    } else {
                        Brush.linearGradient(colors = listOf(GridLine, GridLine))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectServer(server) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CosmicCardElevated else CosmicCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderBrush)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CountryFlagBadge(countryCode = server.countryCode, size = 26.dp)
                            
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = server.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = server.type,
                                        color = if (server.type == "WireGuard") NeonGreen else NeonBlue,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                (if (server.type == "WireGuard") NeonGreen else NeonBlue).copy(
                                                    alpha = 0.12f
                                                )
                                            )
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${server.host}:${server.port}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Dynamic Colored Ping status badge
                            val pingColor = when {
                                server.ping in 0..60 -> NeonGreen
                                server.ping in 61..140 -> NeonBlue
                                server.ping > 140 -> NeonOrange
                                else -> TextSecondary
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (server.ping >= 0) "${server.ping} ms" else "N/A",
                                    color = pingColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(pingColor.copy(alpha = 0.1f))
                                        .clickable { viewModel.testSinglePing(server.id) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )

                                IconButton(
                                    onClick = { viewModel.deleteServer(server) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Server",
                                        tint = CyberPink.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Form dialogue Sheet to append manual profile (VMess, VLess, Shadowsocks, Trojan, WireGuard)
        if (showAddServerDialog) {
            AlertDialog(
                onDismissRequest = { showAddServerDialog = false },
                containerColor = CosmicCard,
                title = {
                    Text(
                        text = LocaleHelper.getString("btn_add_server", lang),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Protocol selector row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("VLess", "VMess", "Shadowsocks", "Trojan", "WireGuard").forEach { type ->
                                val active = serverType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) NeonBlue else CosmicCardElevated)
                                        .clickable { serverType = type }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (type == "Shadowsocks") "SS" else if (type == "WireGuard") "WG" else type,
                                        color = if (active) CosmicBg else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Inputs
                        OutlinedTextField(
                            value = serverName,
                            onValueChange = { serverName = it },
                            label = { Text(LocaleHelper.getString("lbl_server_name", lang), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )

                        OutlinedTextField(
                            value = hostAddress,
                            onValueChange = { hostAddress = it },
                            label = { Text(LocaleHelper.getString("lbl_server_host", lang), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )

                        OutlinedTextField(
                            value = portNumber,
                            onValueChange = { portNumber = it },
                            label = { Text(LocaleHelper.getString("lbl_server_port", lang), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )

                        OutlinedTextField(
                            value = credentialValue,
                            onValueChange = { credentialValue = it },
                            label = { Text(LocaleHelper.getString("lbl_server_uuid", lang), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )

                        // Country Code (DE, JP, FI, US, SG, NL, AE)
                        OutlinedTextField(
                            value = serverCountry,
                            onValueChange = { serverCountry = it.uppercase() },
                            label = { Text("Country Code (e.g. DE, FI, US, SG)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )

                        OutlinedTextField(
                            value = sniHeader,
                            onValueChange = { sniHeader = it },
                            label = { Text("SNI Header (Optional Bypass)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val parsedPort = portNumber.toIntOrNull() ?: 443
                            val finalName = if (serverName.isBlank()) "$serverType Manual Node" else serverName
                            val finalHost = if (hostAddress.isBlank()) "proxy.gate.net" else hostAddress
                            
                            viewModel.addCustomServer(
                                name = finalName,
                                type = serverType,
                                host = finalHost,
                                port = parsedPort,
                                uuid = credentialValue,
                                countryCode = if (serverCountry.isBlank()) "DE" else serverCountry,
                                sni = sniHeader,
                                path = wsPath,
                                security = securityProtocol
                            )
                            
                            // Reset dialog
                            serverName = ""
                            hostAddress = ""
                            portNumber = "443"
                            credentialValue = ""
                            serverCountry = "DE"
                            sniHeader = ""
                            showAddServerDialog = false
                        }
                    ) {
                        Text(LocaleHelper.getString("btn_save", lang), color = NeonBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddServerDialog = false }) {
                        Text(LocaleHelper.getString("btn_cancel", lang), color = TextSecondary)
                    }
                }
            )
        }
    }
}
