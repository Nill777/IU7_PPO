package com.distributed_messenger.domain.irepositories

import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.core.MessageHistory

interface IAppSettingsRepository {
    suspend fun initializeDefaultSettings()
    suspend fun getSetting(type: AppSettingType): Int?
    suspend fun updateSetting(type: AppSettingType, newValue: Int): Boolean
    suspend fun getAllSettings(): List<Pair<AppSettingType, Int>>
    suspend fun addCustomSetting(name: String, defaultValue: Int)
}