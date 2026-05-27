package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1, // Single-row entity
    val language: String = "FA", // "FA" or "EN"
    val vpnMode: String = "GAMING", // "GAMING", "NORMAL", "BYPASS", "GLOBAL"
    val customDns: String = "1.1.1.1, 8.8.8.8",
    val enableMtuOptimization: Boolean = true,
    val gamingMTU: Int = 1400,
    val selectedServerId: Long? = null,
    val killSwitch: Boolean = false,
    val showNotification: Boolean = true,
    val encryptionProtocol: String = "CHACHA20_POLY1305" // "CHACHA20_POLY1305", "AES_256_GCM", "XCHACHA20_POLY1305"
)
