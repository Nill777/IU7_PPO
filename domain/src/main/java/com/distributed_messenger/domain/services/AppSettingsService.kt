package com.distributed_messenger.domain.services

import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.distributed_messenger.domain.irepositories.IAppSettingsRepository
import com.distributed_messenger.domain.iservices.IAppSettingsService

class AppSettingsService(private val repository: IAppSettingsRepository
) : IAppSettingsService {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "AppSettingsService"
    )

    override suspend fun loadSettings(): List<Pair<AppSettingType, Int>> =
        loggingWrapper {
            repository.getAllSettings()
        }

    override suspend fun updateSetting(type: AppSettingType, value: Int): Boolean =
        loggingWrapper {
            repository.updateSetting(type, value)
        }

    override suspend fun initializeDefaultSettings() =
        loggingWrapper {
            repository.initializeDefaultSettings()
        }

    override suspend fun addCustomSetting(name: String, defaultValue: Int) =
        loggingWrapper {
            repository.addCustomSetting(name, defaultValue)
        }
}