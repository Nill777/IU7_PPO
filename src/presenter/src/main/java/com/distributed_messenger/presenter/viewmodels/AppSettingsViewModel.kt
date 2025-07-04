package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.logger.LogLevel
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.domain.iservices.IAppSettingsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppSettingsViewModel(private val service: IAppSettingsService) : ViewModel() {
    private val _themeState = MutableStateFlow(0)
    val themeState: StateFlow<Int> = _themeState
    // Состояние: список пар (Тип настройки, Текущее значение)
    private val _settingsState = MutableStateFlow<List<Pair<AppSettingType, Int>>>(emptyList())
    val settingsState: StateFlow<List<Pair<AppSettingType, Int>>> = _settingsState

    init {
        Logger.log("AppSettingsVM", "Initializing default settings")
        viewModelScope.launch {
            service.initializeDefaultSettings()
            loadSettings()
        }
    }

    private suspend fun loadSettings() {
        Logger.log("AppSettingsVM", "Loading settings from storage")
        try {
            _settingsState.value = service.loadSettings()
            _themeState.value = service.getSetting(AppSettingType.THEME) ?: 0
            Logger.log("AppSettingsVM", "Settings loaded (count: ${_settingsState.value.size})")
        } catch (e: Exception) {
            Logger.log("AppSettingsVM", "Error loading settings: ${e.message}", LogLevel.ERROR, e)
        }
    }

    fun updateSetting(type: AppSettingType, newValue: Int) {
        Logger.log("AppSettingsVM", "Updating setting $type to $newValue")
        viewModelScope.launch {
            if (service.updateSetting(type, newValue)) {
                loadSettings()
                Logger.log("AppSettingsVM", "Setting updated successfully: $type")
            } else {
                Logger.log("AppSettingsVM", "Failed to update setting: $type", LogLevel.WARN)
            }
        }
    }
}