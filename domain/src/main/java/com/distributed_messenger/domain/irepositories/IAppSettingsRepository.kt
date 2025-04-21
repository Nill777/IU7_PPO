package com.distributed_messenger.domain.irepositories

import com.distributed_messenger.core.AppSettingType

interface IAppSettingsRepository {
    suspend fun initializeDefaultSettings()
    suspend fun getAllSettings(): List<Pair<AppSettingType, Int>>
    suspend fun updateSetting(type: AppSettingType, newValue: Int): Boolean
    suspend fun addCustomSetting(name: String, defaultValue: Int)
}