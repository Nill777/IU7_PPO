package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.domain.irepositories.IAppSettingsRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document

class MongoAppSettingsRepository(
    private val collection: MongoCollection<Document>
) : IAppSettingsRepository {

    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoAppSettingsRepository"
    )

    // Метод для инициализации индексов
    suspend fun initializeIndexes() {
        collection.createIndex(
            Indexes.ascending("name"),
            IndexOptions().unique(true)
        )
    }

    override suspend fun initializeDefaultSettings() = loggingWrapper {
        if (collection.countDocuments() == 0L) {
            AppSettingType.entries.forEach { type ->
                val defaultValue = type.possibleValues.keys.first()
                collection.insertOne(
                    Document().apply {
                        put("name", type.settingName)
                        put("value", defaultValue)
                        put("type", type.name)
                    }
                )
            }
        }
    }

    override suspend fun getSetting(type: AppSettingType): Int? = loggingWrapper {
        collection.find(Filters.eq("name", type.settingName))
            .firstOrNull()
            ?.getInteger("value")
    }

    override suspend fun getAllSettings(): List<Pair<AppSettingType, Int>> = loggingWrapper {
        collection.find()
            .toList()
            .mapNotNull { doc ->
                val typeName = doc.getString("type")
                val value = doc.getInteger("value")
                AppSettingType.entries.find { it.name == typeName }?.let { it to value }
            }
    }

    override suspend fun updateSetting(type: AppSettingType, newValue: Int): Boolean = loggingWrapper {
        if (!type.possibleValues.containsKey(newValue)) {
            throw IllegalArgumentException("Invalid value for ${type.settingName}: $newValue")
        }

        collection.updateOne(
            Filters.eq("name", type.settingName),
            Document("\$set", Document("value", newValue))
        ).modifiedCount > 0
    }

    override suspend fun addCustomSetting(name: String, defaultValue: Int) = loggingWrapper {
        if (collection.find(Filters.eq("name", name)).firstOrNull() != null) {
            throw IllegalArgumentException("Setting '$name' already exists")
        }

        collection.insertOne(
            Document().apply {
                put("name", name)
                put("value", defaultValue)
                put("type", "CUSTOM")
            }
        )
        Unit
    }
}