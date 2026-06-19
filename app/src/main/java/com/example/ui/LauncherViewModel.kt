package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.database.LauncherAppItem
import com.example.data.database.LauncherDatabase
import com.example.data.database.LauncherSetting
import com.example.data.database.LauncherWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LaunchableApp(
    val packageName: String,
    val label: String,
    val isSystemMock: Boolean = false,
    val systemIconName: String? = null,
    val customIconColorHex: String? = null,
    var drawable: Drawable? = null,
    val pageIndex: Int = 0,
    val cellIndex: Int = -1,
    val isFavorite: Boolean = false, // True if in bottom dock
    val folderName: String? = null
)

data class StoreApp(
    val packageName: String,
    val label: String,
    val category: String,
    val rating: String,
    val iconColorHex: String,
    val materialIconName: String,
    val description: String,
    var installProgress: Float = -1f, // -1f means not installing, 1f means installed
    val isInstalled: Boolean = false
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val db: LauncherDatabase by lazy {
        Room.databaseBuilder(
            application,
            LauncherDatabase::class.java,
            "ios_launcher_db"
        ).fallbackToDestructiveMigration().build()
    }

    private val dao by lazy { db.dao }

    // Settings State
    val settingsState = MutableStateFlow<Map<String, String>>(emptyMap())

    // App lists
    private val _dbApps = MutableStateFlow<List<LauncherAppItem>>(emptyList())
    private val _systemApps = MutableStateFlow<List<ResolveInfo>>(emptyList())
    
    // Combined final list of apps to display
    private val _appsList = MutableStateFlow<List<LaunchableApp>>(emptyList())
    val appsList: StateFlow<List<LaunchableApp>> = _appsList.asStateFlow()

    // Widgets list
    private val _widgetsList = MutableStateFlow<List<LauncherWidget>>(emptyList())
    val widgetsList: StateFlow<List<LauncherWidget>> = _widgetsList.asStateFlow()

    // UI Interactive States
    val isJiggleMode = MutableStateFlow(false)
    val isControlCenterOpen = MutableStateFlow(false)
    val isSpotlightSearchOpen = MutableStateFlow(false)
    val isAssistiveTouchOpen = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    // Control Center properties
    val isWifiEnabled = MutableStateFlow(true)
    val isBluetoothEnabled = MutableStateFlow(true)
    val isAirplaneMode = MutableStateFlow(false)
    val isFlashlightEnabled = MutableStateFlow(false)
    val screenBrightness = MutableStateFlow(0.8f)
    val volumeLevel = MutableStateFlow(0.6f)
    val isDarkMode = MutableStateFlow(true) // Defaults to deep space dark theme

    // Simulated Active Sub-app
    // When null, we show the Launcher home. When not-null, we display the simulated Apple sub-application!
    val activeSubApp = MutableStateFlow<String?>(null) // "Safari", "Settings", "AppStore", "Weather", etc.

    // Simulated notifications
    val notificationCount = MutableStateFlow(2)

    // Wallpaper configuration
    val activeWallpaper = MutableStateFlow("vibrant") // "vibrant", "classic" or "dark"

    // App Store Mock Catalog
    val storeApps = mutableStateListOf<StoreApp>()

    // Calendar mock events
    val mockEvents = mutableStateListOf(
        "Apple Keynote - 10:00 AM",
        "Lunch with Craig - 1:00 PM",
        "Design Review - 3:30 PM"
    )

    init {
        // Load initial mock Store Apps catalog
        loadStoreCatalog()
        
        // Listen to settings
        viewModelScope.launch {
            dao.getAllSettings().collect { settings ->
                val map = settings.associate { it.key to it.value }
                settingsState.value = map
                
                // Set initial states from DB
                isDarkMode.value = map["dark_mode"]?.toBoolean() ?: true
                activeWallpaper.value = map["wallpaper"] ?: "classic"
            }
        }

        // Listen to Widgets and load defaults if empty
        viewModelScope.launch {
            dao.getAllWidgets().collect { widgets ->
                if (widgets.isEmpty()) {
                    // Seed standard default beautiful iOS widgets
                    val defaultWidgets = listOf(
                        LauncherWidget(type = "WEATHER", pageIndex = 0, sizeX = 2, sizeY = 2),
                        LauncherWidget(type = "BATTERY", pageIndex = 0, sizeX = 2, sizeY = 2),
                        LauncherWidget(type = "CLOCK", pageIndex = 1, sizeX = 2, sizeY = 2),
                        LauncherWidget(type = "CALENDAR", pageIndex = 1, sizeX = 2, sizeY = 2)
                    )
                    defaultWidgets.forEach { dao.saveWidget(it) }
                } else {
                    _widgetsList.value = widgets
                }
            }
        }

        // Fetch system installed applications
        loadLauncherApps()
    }

    private fun loadStoreCatalog() {
        storeApps.clear()
        storeApps.addAll(
            listOf(
                StoreApp("com.whatsapp", "WhatsApp", "Social Networking", "4.8 ★", "#25D366", "chat", "Simple, secure, reliable messaging."),
                StoreApp("com.instagram.android", "Instagram", "Photo & Video", "4.7 ★", "#E1306C", "photo_camera", "Bring you closer to the people and things you love."),
                StoreApp("com.spotify.music", "Spotify", "Music", "4.9 ★", "#1DB954", "music_note", "Millions of songs and podcasts at your fingertips."),
                StoreApp("com.netflix.mediaclient", "Netflix", "Entertainment", "4.6 ★", "#E50914", "movie", "Watch TV shows and movies recommended just for you."),
                StoreApp("com.google.android.youtube", "YouTube", "Entertainment", "4.8 ★", "#FF0000", "play_circle", "See what the world is watching in music, gaming, and more."),
                StoreApp("com.tiktok.android", "TikTok", "Entertainment", "4.5 ★", "#000000", "theater_comedy", "Real videos. Real people. Express yourself creatively.")
            )
        )
    }

    private fun loadLauncherApps() {
        viewModelScope.launch {
            // Get physical packages
            val context = getApplication<Application>()
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            
            val physicalApps = withContext(Dispatchers.IO) {
                pm.queryIntentActivities(intent, 0)
            }
            _systemApps.value = physicalApps

            // Sync with DB
            dao.getAllApps().collect { savedItems ->
                _dbApps.value = savedItems
                combineApps(savedItems, physicalApps, pm)
            }
        }
    }

    // Combine database user definitions and real physical apps to map them into Launcher Pages
    private fun combineApps(
        savedItems: List<LauncherAppItem>,
        physicalApps: List<ResolveInfo>,
        pm: PackageManager
    ) {
        val appList = ArrayList<LaunchableApp>()

        // 1. ADD BUNDLED CORE iOS SIMULATION APPS
        // These apps are guaranteed to exist to provide the rich iOS simulation experience!
        val bundledApps = listOf(
            LaunchableApp("com.apple.safari", "Safari", isSystemMock = true, systemIconName = "Safari", customIconColorHex = "#2196F3", pageIndex = 0, cellIndex = 0),
            LaunchableApp("com.apple.weather", "Weather", isSystemMock = true, systemIconName = "Weather", customIconColorHex = "#03A9F4", pageIndex = 0, cellIndex = 1),
            LaunchableApp("com.apple.photos", "Photos", isSystemMock = true, systemIconName = "Photos", customIconColorHex = "#E91E63", pageIndex = 0, cellIndex = 2),
            LaunchableApp("com.apple.store", "App Store", isSystemMock = true, systemIconName = "AppStore", customIconColorHex = "#00BCD4", pageIndex = 0, cellIndex = 3),
            LaunchableApp("com.apple.settings", "Settings", isSystemMock = true, systemIconName = "Settings", customIconColorHex = "#9E9E9E", pageIndex = 0, cellIndex = 4),
            LaunchableApp("com.apple.camera", "Camera", isSystemMock = true, systemIconName = "Camera", customIconColorHex = "#3F51B5", pageIndex = 0, cellIndex = 5),
            LaunchableApp("com.apple.maps", "Maps", isSystemMock = true, systemIconName = "Maps", customIconColorHex = "#4CAF50", pageIndex = 0, cellIndex = 6),
            LaunchableApp("com.apple.calendar", "Calendar", isSystemMock = true, systemIconName = "Calendar", customIconColorHex = "#F44336", pageIndex = 0, cellIndex = 7),
            LaunchableApp("com.apple.clock", "Clock", isSystemMock = true, systemIconName = "Clock", customIconColorHex = "#1A1A1A", pageIndex = 0, cellIndex = 8, isFavorite = true), // dock
            LaunchableApp("com.apple.health", "Health", isSystemMock = true, systemIconName = "Health", customIconColorHex = "#FF5252", pageIndex = 1, cellIndex = 2)
        )

        // Preload database definitions for bundled apps if missing
        viewModelScope.launch(Dispatchers.IO) {
            bundledApps.forEach { bundled ->
                val match = savedItems.find { it.packageName == bundled.packageName }
                if (match == null) {
                    dao.saveAppItem(
                        LauncherAppItem(
                            packageName = bundled.packageName,
                            customLabel = bundled.label,
                            pageIndex = bundled.pageIndex,
                            cellIndex = bundled.cellIndex,
                            isSystemMock = true,
                            isFavorite = bundled.isFavorite
                        )
                    )
                }
            }
        }

        // Incorporate saved settings to bundled apps
        bundledApps.forEach { bundled ->
            val saved = savedItems.find { it.packageName == bundled.packageName }
            if (saved != null) {
                appList.add(
                    bundled.copy(
                        label = saved.customLabel ?: bundled.label,
                        pageIndex = saved.pageIndex,
                        cellIndex = saved.cellIndex,
                        isFavorite = saved.isFavorite,
                        folderName = saved.folderName
                    )
                )
            } else {
                appList.add(bundled)
            }
        }

        // 2. ADD REAL PHYSICAL ANDROID INSTALLED APPS (Excluding the launcher packages itself)
        var nextPageIndex = 1
        var nextCellIndex = 0

        physicalApps.forEach { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            // Skip launcher app itself
            if (pkg == getApplication<Application>().packageName) return@forEach

            val label = resolveInfo.loadLabel(pm).toString()
            val drawable = resolveInfo.loadIcon(pm)

            val saved = savedItems.find { it.packageName == pkg }
            if (saved != null) {
                if (!saved.isHidden) {
                    appList.add(
                        LaunchableApp(
                            packageName = pkg,
                            label = saved.customLabel ?: label,
                            drawable = drawable,
                            pageIndex = saved.pageIndex,
                            cellIndex = saved.cellIndex,
                            isFavorite = saved.isFavorite,
                            folderName = saved.folderName
                        )
                    )
                }
            } else {
                // Determine layout allocation dynamically
                while (isCellOccupied(appList, nextPageIndex, nextCellIndex)) {
                    nextCellIndex++
                    if (nextCellIndex >= 24) { // 4x6 grid per screen page
                        nextCellIndex = 0
                        nextPageIndex++
                    }
                }
                
                // Allocate and Persist layout
                val allocatedCell = nextCellIndex
                val allocatedPage = nextPageIndex
                nextCellIndex++
                if (nextCellIndex >= 24) {
                    nextCellIndex = 0
                    nextPageIndex++
                }

                viewModelScope.launch(Dispatchers.IO) {
                    dao.saveAppItem(
                        LauncherAppItem(
                            packageName = pkg,
                            customLabel = label,
                            pageIndex = allocatedPage,
                            cellIndex = allocatedCell
                        )
                    )
                }

                appList.add(
                    LaunchableApp(
                        packageName = pkg,
                        label = label,
                        drawable = drawable,
                        pageIndex = allocatedPage,
                        cellIndex = allocatedCell
                    )
                )
            }
        }

        // Sync local downloaded App Store catalog items
        storeApps.forEachIndexed { i, storeAppName ->
            val saved = savedItems.find { it.packageName == storeAppName.packageName }
            if (saved != null) {
                // This app was "installed" by user inside App Store!
                storeApps[i] = storeAppName.copy(isInstalled = true, installProgress = 1f)
                
                // Add to Launcher Apps list if not already there
                if (appList.none { it.packageName == storeAppName.packageName }) {
                    appList.add(
                        LaunchableApp(
                            packageName = storeAppName.packageName,
                            label = saved.customLabel ?: storeAppName.label,
                            isSystemMock = true,
                            systemIconName = storeAppName.materialIconName,
                            customIconColorHex = storeAppName.iconColorHex,
                            pageIndex = saved.pageIndex,
                            cellIndex = saved.cellIndex,
                            isFavorite = saved.isFavorite,
                            folderName = saved.folderName
                        )
                    )
                }
            }
        }

        _appsList.value = appList
    }

    private fun isCellOccupied(apps: List<LaunchableApp>, page: Int, cell: Int): Boolean {
        return apps.any { it.pageIndex == page && it.cellIndex == cell }
    }

    // Toggle Setting Value in Room
    fun toggleSetting(key: String, defaultValue: Boolean) {
        viewModelScope.launch {
            val currentValue = settingsState.value[key]?.toBoolean() ?: defaultValue
            val newValue = !currentValue
            dao.saveSetting(LauncherSetting(key, newValue.toString()))
            
            // Sync specific setting states immediately
            if (key == "dark_mode") {
                isDarkMode.value = newValue
            }
        }
    }

    // Save Wallpaper to settings
    fun selectWallpaper(wallpaperName: String) {
        viewModelScope.launch {
            dao.saveSetting(LauncherSetting("wallpaper", wallpaperName))
            activeWallpaper.value = wallpaperName
        }
    }

    // Launch Selected App
    fun launchApp(context: Context, app: LaunchableApp) {
        if (app.isSystemMock) {
            // Trigger simulated iOS inner screen flow
            activeSubApp.value = app.systemIconName
            isControlCenterOpen.value = false
            isSpotlightSearchOpen.value = false
        } else {
            // Physical Launch
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
    }

    // Uninstall App (hidden label)
    fun hideOrUninstallApp(pkg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val app = dao.getAppByPackageName(pkg)
            if (app != null) {
                if (app.isSystemMock) {
                    // Hidden
                    dao.saveAppItem(app.copy(isHidden = true))
                } else {
                    // Removed entirely
                    dao.deleteAppItem(pkg)
                }
            }
        }
    }

    // Custom Drag rearrangement simulation (move app coordinate)
    fun moveAppGridPosition(packageName: String, targetPage: Int, targetCell: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val app = dao.getAppByPackageName(packageName)
            if (app != null) {
                dao.saveAppItem(app.copy(pageIndex = targetPage, cellIndex = targetCell))
            }
        }
    }

    // Toggle favorite state (placing on the bottom floating bar dock)
    fun toggleDockFavorite(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val app = dao.getAppByPackageName(packageName)
            if (app != null) {
                dao.saveAppItem(app.copy(isFavorite = !app.isFavorite))
            }
        }
    }

    // Create / edit folder
    fun addAppToFolder(packageName: String, folder: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val app = dao.getAppByPackageName(packageName)
            if (app != null) {
                dao.saveAppItem(app.copy(folderName = folder))
            }
        }
    }

    // SIMULATED SYSTEM ACTIONS
    fun addWidget(type: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.saveWidget(LauncherWidget(type = type, pageIndex = page, sizeX = 2, sizeY = 2))
        }
    }

    fun removeWidget(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWidget(id)
        }
    }

    // Simulated download of app in App Store
    fun installAppFromStore(app: StoreApp) {
        val index = storeApps.indexOfFirst { it.packageName == app.packageName }
        if (index == -1) return

        viewModelScope.launch {
            // Simulated animated download ticks
            for (progress in 0..5) {
                storeApps[index] = storeApps[index].copy(installProgress = progress / 5f)
                kotlinx.coroutines.delay(400)
            }
            
            // Persist app list update
            withContext(Dispatchers.IO) {
                // Find empty slot
                val apps = _appsList.value
                var page = 1
                var cell = 0
                while (isCellOccupied(apps, page, cell)) {
                    cell++
                    if (cell >= 24) {
                        cell = 0
                        page++
                    }
                }
                
                dao.saveAppItem(
                    LauncherAppItem(
                        packageName = app.packageName,
                        customLabel = app.label,
                        pageIndex = page,
                        cellIndex = cell,
                        isSystemMock = true
                    )
                )
            }
        }
    }

    fun addCalendarEvent(event: String) {
        mockEvents.add(event)
    }

    // Factory Class for robust viewmodel injections
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LauncherViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
