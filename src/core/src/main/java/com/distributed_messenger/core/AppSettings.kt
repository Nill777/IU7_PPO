package com.distributed_messenger.core

enum class AppSettingType(val settingName: String, val possibleValues: Map<Int, String>) {
    MAX_FILE_SIZE(
        settingName = "Максимальный размер файла",
        possibleValues = mapOf(
            10 to "10 МБ",
            50 to "50 МБ",
            100 to "100 МБ"
        )
    ),
    MESSAGE_ENCRYPTION(
        settingName = "Шифрование сообщений",
        possibleValues = mapOf(
            0 to "Выключено",
            1 to "Включено"
        )
    ),
    HISTORY_STORAGE(
        settingName = "Хранение истории",
        possibleValues = mapOf(
            7 to "7 дней",
            30 to "30 дней",
            365 to "1 год"
        )
    ),
    THEME(
        settingName = "Тема приложения",
        possibleValues = mapOf(
            0 to "Системная",
            1 to "Светлая",
            2 to "Тёмная"
        )
    );
}

data class AppSettings(
    val id: Int,
    val name: String,
    val value: Int
)