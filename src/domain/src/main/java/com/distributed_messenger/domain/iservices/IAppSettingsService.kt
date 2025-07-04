package com.distributed_messenger.domain.iservices

import com.distributed_messenger.core.AppSettingType

interface IAppSettingsService {
    suspend fun loadSettings(): List<Pair<AppSettingType, Int>>
    suspend fun getSetting(type: AppSettingType): Int?
    suspend fun updateSetting(type: AppSettingType, value: Int): Boolean
    suspend fun initializeDefaultSettings()
    suspend fun addCustomSetting(name: String, defaultValue: Int)
}