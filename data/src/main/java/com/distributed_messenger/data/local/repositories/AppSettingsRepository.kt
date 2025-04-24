package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.distributed_messenger.data.local.dao.AppSettingsDao
import com.distributed_messenger.data.local.entities.AppSettingsEntity
import com.distributed_messenger.domain.irepositories.IAppSettingsRepository

class AppSettingsRepository(private val dao: AppSettingsDao) : IAppSettingsRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "AppSettingsRepository"
    )

    override suspend fun initializeDefaultSettings() =
        loggingWrapper {
            if (dao.count() == 0) {
                AppSettingType.entries.forEach { type ->
                    val defaultValue = type.possibleValues.keys.first()
                    dao.insert(
                        AppSettingsEntity(
                            name = type.settingName,
                            value = defaultValue
                        )
                    )
                }
            }
        }

    override suspend fun getAllSettings(): List<Pair<AppSettingType, Int>> =
        loggingWrapper {
            dao.getAll().mapNotNull { entity ->
                AppSettingType.entries.find { it.settingName == entity.name }?.let { type ->
                    type to entity.value
                }
            }
        }

    override suspend fun updateSetting(type: AppSettingType, newValue: Int): Boolean =
        loggingWrapper {
            if (!type.possibleValues.containsKey(newValue)) {
                throw IllegalArgumentException("Недопустимое значение для ${type.settingName}: $newValue")
            }

            val entity = dao.getByName(type.settingName) ?: return@loggingWrapper false
            return@loggingWrapper dao.update(entity.copy(value = newValue)) > 0
        }

    override suspend fun addCustomSetting(name: String, defaultValue: Int) =
        loggingWrapper {
            if (dao.getByName(name) != null) {
                throw IllegalArgumentException("Настройка '$name' уже существует")
            }
            dao.insert(AppSettingsEntity(name = name, value = defaultValue))
        }
}
