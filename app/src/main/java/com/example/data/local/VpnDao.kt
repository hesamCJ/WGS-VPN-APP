package com.example.data.local

import androidx.room.*
import com.example.data.models.AppSettings
import com.example.data.models.Subscription
import com.example.data.models.VpnServer
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnDao {

    // --- Subscriptions ---
    @Query("SELECT * FROM subscriptions ORDER BY id DESC")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Delete
    suspend fun deleteSubscription(subscription: Subscription)

    @Query("DELETE FROM subscriptions")
    suspend fun clearAllSubscriptions()

    // --- VPN Servers ---
    @Query("SELECT * FROM vpn_servers ORDER BY id DESC")
    fun getAllServers(): Flow<List<VpnServer>>

    @Query("SELECT * FROM vpn_servers WHERE subId = :subId ORDER BY id DESC")
    fun getServersBySubscription(subId: Long): Flow<List<VpnServer>>

    @Query("SELECT * FROM vpn_servers WHERE isSelected = 1 LIMIT 1")
    fun getSelectedServerFlow(): Flow<VpnServer?>

    @Query("SELECT * FROM vpn_servers WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedServer(): VpnServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpnServer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpnServer>)

    @Update
    suspend fun updateServer(server: VpnServer)

    @Delete
    suspend fun deleteServer(server: VpnServer)

    @Query("DELETE FROM vpn_servers WHERE subId = :subId")
    suspend fun deleteServersBySubscription(subId: Long)

    @Query("DELETE FROM vpn_servers")
    suspend fun clearAllServers()

    @Transaction
    suspend fun selectServer(serverId: Long) {
        // Clear previous selected server
        resetServerSelection()
        // Set new selected server
        markServerAsSelected(serverId)
    }

    @Query("UPDATE vpn_servers SET isSelected = 0")
    suspend fun resetServerSelection()

    @Query("UPDATE vpn_servers SET isSelected = 1 WHERE id = :serverId")
    suspend fun markServerAsSelected(serverId: Long)

    @Query("UPDATE vpn_servers SET ping = :ping WHERE id = :serverId")
    suspend fun updateServerPing(serverId: Long, ping: Int)


    // --- App Settings (Single Row) ---
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getAppSettingsFlow(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getAppSettings(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(settings: AppSettings)
}
