package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.models.AppSettings
import com.example.data.models.Subscription
import com.example.data.models.VpnServer
import com.example.data.repository.VpnRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VpnRepository
    
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }

    // Database Flows
    val allServers: StateFlow<List<VpnServer>>
    val allSubscriptions: StateFlow<List<Subscription>>
    val selectedServer: StateFlow<VpnServer?>
    val appSettings: StateFlow<AppSettings>

    // Connection States
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Live Speeds (in KB/s)
    private val _downloadSpeed = MutableStateFlow(0.0)
    val downloadSpeed: StateFlow<Double> = _downloadSpeed.asStateFlow()

    private val _uploadSpeed = MutableStateFlow(0.0)
    val uploadSpeed: StateFlow<Double> = _uploadSpeed.asStateFlow()

    // Activity stats tracker
    private val _totalDownloadedBytes = MutableStateFlow(0L)
    val totalDownloadedBytes: StateFlow<Long> = _totalDownloadedBytes.asStateFlow()

    // Async Testing indicators
    private val _isTestingPings = MutableStateFlow(false)
    val isTestingPings: StateFlow<Boolean> = _isTestingPings.asStateFlow()

    // Event alerts for the UI (like showing toast notices)
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Dynamic Connection simulation job
    private var trafficSimulatorJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VpnRepository(database.vpnDao())

        // Setup base states from DB
        val serversStateFlow = MutableStateFlow<List<VpnServer>>(emptyList())
        allServers = serversStateFlow
        
        val subsStateFlow = MutableStateFlow<List<Subscription>>(emptyList())
        allSubscriptions = subsStateFlow

        val selectedServerStateFlow = MutableStateFlow<VpnServer?>(null)
        selectedServer = selectedServerStateFlow

        val settingsStateFlow = MutableStateFlow(AppSettings())
        appSettings = settingsStateFlow

        viewModelScope.launch {
            repository.initializeSettings()
            
            // Launch collection jobs to keep StateFlows sync'd
            launch {
                repository.allServers.collect { serversStateFlow.value = it }
            }
            launch {
                repository.allSubscriptions.collect { subsStateFlow.value = it }
            }
            launch {
                repository.selectedServer.collect { selectedServerStateFlow.value = it }
            }
            launch {
                repository.appSettings.collect { settings ->
                    if (settings != null) {
                        settingsStateFlow.value = settings
                    }
                }
            }
        }
    }

    // Toggle Connection Connect/Disconnect
    fun toggleConnection() {
        viewModelScope.launch {
            val currentState = _connectionState.value
            if (currentState == ConnectionState.DISCONNECTED) {
                _connectionState.value = ConnectionState.CONNECTING
                delay(1200) // Realistic connection negotiation simulation (v2ray protocol handshake)
                _connectionState.value = ConnectionState.CONNECTED
                _uiEvents.emit("WGS_VPN: Connected")
                startTrafficSimulation()
            } else if (currentState == ConnectionState.CONNECTED) {
                _connectionState.value = ConnectionState.DISCONNECTING
                stopTrafficSimulation()
                delay(800)
                _connectionState.value = ConnectionState.DISCONNECTED
                _uiEvents.emit("WGS_VPN: Disconnected")
            }
        }
    }

    private fun startTrafficSimulation() {
        trafficSimulatorJob?.cancel()
        trafficSimulatorJob = viewModelScope.launch {
            // Seed base numbers
            val settings = appSettings.value
            var baseDownload = if (settings.vpnMode == "GAMING") 940.0 else 420.0
            var baseUpload = if (settings.vpnMode == "GAMING") 180.0 else 120.0

            while (true) {
                // Introduce natural fluctuative traffic spikes and lulls
                val fluctuationDown = Random.nextDouble(-120.0, 240.0)
                val fluctuationUp = Random.nextDouble(-30.0, 60.0)

                val activeDown = maxOf(25.0, baseDownload + fluctuationDown)
                val activeUp = maxOf(8.0, baseUpload + fluctuationUp)

                _downloadSpeed.value = activeDown
                _uploadSpeed.value = activeUp
                
                _totalDownloadedBytes.value += (activeDown * 1024).toLong()

                delay(1000)
            }
        }
    }

    private fun stopTrafficSimulation() {
        trafficSimulatorJob?.cancel()
        _downloadSpeed.value = 0.0
        _uploadSpeed.value = 0.0
    }

    // Test pings across all servers sequentially
    fun testAllPings() {
        viewModelScope.launch {
            if (_isTestingPings.value) return@launch
            _isTestingPings.value = true
            _uiEvents.emit("Testing pings...")
            
            val currentServers = allServers.value
            currentServers.forEach { server ->
                repository.testServerPing(server.id)
            }
            
            _isTestingPings.value = false
            _uiEvents.emit("Smart auto-sort complete based on latency.")
        }
    }

    // Trigger individual single-server ping test from UI
    fun testSinglePing(serverId: Long) {
        viewModelScope.launch {
            val freshPing = repository.testServerPing(serverId)
            _uiEvents.emit("Latency: ${freshPing}ms")
        }
    }

    // Select Server profile
    fun selectServer(server: VpnServer) {
        viewModelScope.launch {
            repository.selectServer(server.id)
            // If connected, restart connection to target the new destination node
            if (_connectionState.value == ConnectionState.CONNECTED) {
                _connectionState.value = ConnectionState.CONNECTING
                stopTrafficSimulation()
                delay(1000)
                _connectionState.value = ConnectionState.CONNECTED
                startTrafficSimulation()
            }
        }
    }

    // Add subscription URL
    fun addSubscription(name: String, url: String) {
        viewModelScope.launch {
            try {
                if (url.isBlank() || name.isBlank()) {
                    _uiEvents.emit("Please enter valid name and link URL")
                    return@launch
                }
                repository.importFromSubscription(name, url)
                _uiEvents.emit("Subscription imported with active servers!")
            } catch (e: Exception) {
                _uiEvents.emit("Failed to load subscription")
            }
        }
    }

    // Delete Subscription
    fun deleteSubscription(sub: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(sub)
            _uiEvents.emit("Subscription removed")
        }
    }

    // Paste base64 share links
    fun importFromClipboard(rawLink: String) {
        viewModelScope.launch {
            val ok = repository.parseAndImportShareLink(rawLink)
            if (ok) {
                _uiEvents.emit("Config parsed and added successfully!")
            } else {
                _uiEvents.emit("Error: Config link not supported or invalid format")
            }
        }
    }

    // Custom Server insertion
    fun addCustomServer(
        name: String,
        type: String,
        host: String,
        port: Int,
        uuid: String,
        countryCode: String,
        sni: String,
        path: String,
        security: String
    ) {
        viewModelScope.launch {
            val server = VpnServer(
                name = name,
                type = type,
                host = host,
                port = port,
                uuidOrPassword = uuid,
                countryCode = countryCode,
                sni = sni,
                path = path,
                security = security,
                isSelected = false
            )
            repository.addCustomServer(server)
            _uiEvents.emit("Server configuration saved manually")
        }
    }

    fun deleteServer(server: VpnServer) {
        viewModelScope.launch {
            repository.deleteServer(server)
            _uiEvents.emit("Server deleted")
        }
    }

    // Save customized global configurations of DNS, Mode, MTU
    fun updateSettings(
        language: String,
        mode: String,
        customDns: String,
        mtuOptimize: Boolean,
        gamingMTU: Int,
        killSwitch: Boolean,
        encryptionProtocol: String
    ) {
        viewModelScope.launch {
            val updated = appSettings.value.copy(
                language = language,
                vpnMode = mode,
                customDns = customDns,
                enableMtuOptimization = mtuOptimize,
                gamingMTU = gamingMTU,
                killSwitch = killSwitch,
                encryptionProtocol = encryptionProtocol
            )
            repository.saveAppSettings(updated)
            _uiEvents.emit("Settings updated applied successfully!")
            
            // If connected and settings changed, hot-reload simulation numbers
            if (_connectionState.value == ConnectionState.CONNECTED) {
                startTrafficSimulation()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        trafficSimulatorJob?.cancel()
    }
}
