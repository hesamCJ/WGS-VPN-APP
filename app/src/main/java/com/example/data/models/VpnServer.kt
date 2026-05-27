package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subId: Long? = null, // null if custom manually added
    val name: String,
    val type: String, // VMess, VLess, Shadowsocks, Trojan, WireGuard
    val host: String,
    val port: Int,
    val uuidOrPassword: String,
    val encryption: String = "AES-128-GCM",
    val ping: Int = -1, // in milliseconds, -1 = not tested
    val countryCode: String = "US", // US, DE, GB, FI, SG, JP, TR, AE, etc.
    val isSelected: Boolean = false,
    val sni: String = "",
    val path: String = "",
    val security: String = "tls",
    val createdAt: Long = System.currentTimeMillis()
)
