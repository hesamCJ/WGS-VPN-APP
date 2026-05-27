package com.example.ui.vpn

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppSettings
import com.example.data.models.Subscription
import com.example.ui.localization.LocaleHelper
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: VpnViewModel,
    settings: AppSettings
) {
    val subscriptions by viewModel.allSubscriptions.collectAsState()
    val lang = settings.language

    var showAddDialog by remember { mutableStateOf(false) }
    var subName by remember { mutableStateOf("") }
    var subUrl by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        
        // Subscription Overview / Summary header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "FA") "مدیریت اشتراک‌های هوشمند" else "Smart Subscription Manager",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == "FA") 
                        "آدرس‌های اشتراک پکیج کانفیگ‌ها را اضافه کنید. برنامه به صورت خودکار تغییرات را با پینگ بهینه همگام‌سازی می‌کند."
                        else "Add V2Ray subscription urls. WGS_VPN automatically aggregates and groups active nodes.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Add Sub Trigger Button
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryAdd,
                        contentDescription = "Add sub",
                        tint = CosmicBg,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleHelper.getString("btn_add_sub", lang),
                        color = CosmicBg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Subscriptions List
        if (subscriptions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = "No Subs",
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = LocaleHelper.getString("lbl_empty_subs", lang),
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
                items(subscriptions, key = { it.id }) { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = NeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sub.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteSubscription(sub) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete sub",
                                        tint = CyberPink.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = sub.url,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = GridLine, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Updated: ${dateFormat.format(Date(sub.lastUpdated))}",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )

                                Text(
                                    text = "${sub.serverCount} servers",
                                    color = NeonBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonBlue.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action additions dialog form
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = CosmicCard,
                title = {
                    Text(
                        text = LocaleHelper.getString("btn_add_sub", lang),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = subName,
                            onValueChange = { subName = it },
                            placeholder = { Text("e.g. Free Fast Server Subs", fontSize = 12.sp) },
                            label = { Text(LocaleHelper.getString("lbl_sub_name", lang), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue,
                                unfocusedBorderColor = GridLine
                            )
                        )

                        OutlinedTextField(
                            value = subUrl,
                            onValueChange = { subUrl = it },
                            placeholder = { Text("https://wgs-config-provider.com/sub", fontSize = 12.sp) },
                            label = { Text(LocaleHelper.getString("lbl_sub_url", lang), fontSize = 11.sp) },
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
                            viewModel.addSubscription(subName, subUrl)
                            subName = ""
                            subUrl = ""
                            showAddDialog = false
                        }
                    ) {
                        Text(LocaleHelper.getString("btn_save", lang), color = NeonBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(LocaleHelper.getString("btn_cancel", lang), color = TextSecondary)
                    }
                }
            )
        }
    }
}
