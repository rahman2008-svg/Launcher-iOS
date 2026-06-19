package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "launcher_settings")
data class LauncherSetting(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "launcher_app_items")
data class LauncherAppItem(
    @PrimaryKey val packageName: String,
    val customLabel: String? = null,
    val pageIndex: Int = 0,
    val cellIndex: Int = -1,
    val isHidden: Boolean = false,
    val folderName: String? = null,
    val isSystemMock: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "launcher_widgets")
data class LauncherWidget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "WEATHER", "CLOCK", "BATTERY", "CALENDAR"
    val pageIndex: Int = 0,
    val sizeX: Int = 2,
    val sizeY: Int = 2
)

@Dao
interface LauncherDao {
    @Query("SELECT * FROM launcher_settings")
    fun getAllSettings(): Flow<List<LauncherSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: LauncherSetting)

    @Query("SELECT * FROM launcher_app_items")
    fun getAllApps(): Flow<List<LauncherAppItem>>

    @Query("SELECT * FROM launcher_app_items WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppByPackageName(packageName: String): LauncherAppItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppItem(app: LauncherAppItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppItems(apps: List<LauncherAppItem>)

    @Query("DELETE FROM launcher_app_items WHERE packageName = :packageName")
    suspend fun deleteAppItem(packageName: String)

    @Query("SELECT * FROM launcher_widgets")
    fun getAllWidgets(): Flow<List<LauncherWidget>>

    @Query("SELECT * FROM launcher_widgets WHERE id = :id LIMIT 1")
    suspend fun getWidgetById(id: Long): LauncherWidget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWidget(widget: LauncherWidget)

    @Query("DELETE FROM launcher_widgets WHERE id = :id")
    suspend fun deleteWidget(id: Long)
}

@Database(
    entities = [LauncherSetting::class, LauncherAppItem::class, LauncherWidget::class],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract val dao: LauncherDao
}
