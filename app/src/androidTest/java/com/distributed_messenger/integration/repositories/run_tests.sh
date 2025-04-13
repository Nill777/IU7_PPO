#!/bin/bash

cd "/home/andrey/AndroidStudioProjects/IU7_PPO" || exit

if [ ! -f "gradlew" ]; then
    echo "Ошибка: gradlew не найден в корне проекта!"
    exit 1
fi

# Массив классов для тестов
test_classes=(
    "com.distributed_messenger.integration.repositories.UserRepositoryIntegrationTest"
    "com.distributed_messenger.integration.repositories.ChatRepositoryIntegrationTest"
    "com.distributed_messenger.integration.repositories.FileRepositoryIntegrationTest"
    "com.distributed_messenger.integration.repositories.MessageRepositoryIntegrationTest"
    "com.distributed_messenger.integration.repositories.BlockRepositoryIntegrationTest"
)

# Запуск тестов поочередно
for test_class in "${test_classes[@]}"; do
    echo "Запуск тестов для класса: $test_class"
    ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class="$test_class"
done