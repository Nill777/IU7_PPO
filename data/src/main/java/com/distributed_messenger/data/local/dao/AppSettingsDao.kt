package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.distributed_messenger.data.local.entities.AppSettingsEntity

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings")
    suspend fun getAll(): List<AppSettingsEntity>

    /*
    OnConflictStrategy.IGNORE означает, что если возникает конфликт
    (например, вы пытаетесь вставить запись с уже существующим setting_name),
    то Room просто игнорирует эту операцию вставки и не добавляет новую запись.
    При этом существующая запись остается без изменений.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(setting: AppSettingsEntity)

    @Update
    suspend fun update(setting: AppSettingsEntity): Int

    @Query("SELECT COUNT(*) FROM app_settings")
    suspend fun count(): Int

    @Query("SELECT * FROM app_settings WHERE setting_name = :name")
    suspend fun getByName(name: String): AppSettingsEntity?
}