package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.domain.iservices.IAppSettingsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppSettingsViewModel(private val service: IAppSettingsService) : ViewModel() {

    // Состояние: список пар (Тип настройки, Текущее значение)
    private val _settingsState = MutableStateFlow<List<Pair<AppSettingType, Int>>>(emptyList())
    val settingsState: StateFlow<List<Pair<AppSettingType, Int>>> = _settingsState

    init {
        viewModelScope.launch {
            service.initializeDefaultSettings()
            loadSettings()
        }
    }

    private suspend fun loadSettings() {
        _settingsState.value = service.loadSettings()
    }

    fun updateSetting(type: AppSettingType, newValue: Int) {
        viewModelScope.launch {
            if (service.updateSetting(type, newValue)) {
                loadSettings()
            }
        }
    }
}