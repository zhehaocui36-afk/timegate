package com.timegate.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 受管应用 DAO（R-100 / R-13）。
 */
@Dao
interface AppPolicyDao {

    /** 观察受管应用列表（managed = true）。 */
    @Query("SELECT * FROM app_policy WHERE managed = 1 ORDER BY display_name")
    fun observeManagedApps(): Flow<List<AppPolicyEntity>>

    /** 一次性读取受管应用列表。 */
    @Query("SELECT * FROM app_policy WHERE managed = 1 ORDER BY display_name")
    suspend fun getManagedApps(): List<AppPolicyEntity>

    /** 按包名读取单条（含不受管的）。 */
    @Query("SELECT * FROM app_policy WHERE package_name = :packageName")
    suspend fun getApp(packageName: String): AppPolicyEntity?

    /** 插入或整行覆盖。 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(app: AppPolicyEntity)

    /** 改名。 */
    @Query("UPDATE app_policy SET display_name = :newName WHERE package_name = :packageName")
    suspend fun rename(packageName: String, newName: String)

    /** 开关「受管」状态。 */
    @Query("UPDATE app_policy SET managed = :managed WHERE package_name = :packageName")
    suspend fun setManaged(packageName: String, managed: Boolean)

    /** 删除（移除受管应用）。 */
    @Delete
    suspend fun delete(app: AppPolicyEntity)
}
