package com.distributed_messenger.domain.services

import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.domain.irepositories.IAppSettingsRepository
import com.distributed_messenger.domain.iservices.IAppSettingsService

class AppSettingsService(
    private val repository: IAppSettingsRepository
) : IAppSettingsService {
    override suspend fun loadSettings(): List<Pair<AppSettingType, Int>> {
        return repository.getAllSettings()
    }

    override suspend fun updateSetting(type: AppSettingType, value: Int): Boolean {
        return repository.updateSetting(type, value)
    }

    override suspend fun initializeDefaultSettings() {
        repository.initializeDefaultSettings()
    }

    override suspend fun addCustomSetting(name: String, defaultValue: Int) {
        repository.addCustomSetting(name, defaultValue)
    }
}