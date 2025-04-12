package com.distributed_messenger.integration.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.domain.models.File
import com.distributed_messenger.implementation.repositories.FileRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

/*
Положительные тесты:
    Добавление файла и проверка его наличия в БД (addFile).
    Обновление метаданных файла (например, имени).
    Удаление файла и проверка его отсутствия.
    Получение всех файлов и фильтрация по пользователю.
Отрицательные тесты:
    Попытка получить/обновить/удалить несуществующий файл.
    Обработка конфликтов при добавлении дубликатов.
*/

class FileRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: FileRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = FileRepository(database.fileDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Положительные тесты

    @Test
    fun addFile_should_insert_file_and_return_id() = runTest {
        val file = createTestFile()

        val insertedId = repository.addFile(file)
        val fetchedFile = repository.getFile(insertedId)

        assertEquals(file.id, insertedId)
        assertEquals(file, fetchedFile)
    }

    @Test
    fun updateFile_should_modify_existing_file() = runTest {
        val file = createTestFile().apply { repository.addFile(this) }
        val updatedFile = file.copy(name = "new_file.txt")

        val isUpdated = repository.updateFile(updatedFile)
        val fetchedFile = repository.getFile(file.id)

        assertTrue(isUpdated)
        assertEquals("new_file.txt", fetchedFile?.name)
    }

    @Test
    fun deleteFile_should_remove_file_from_db() = runTest {
        val file = createTestFile().apply { repository.addFile(this) }

        val isDeleted = repository.deleteFile(file.id)
        val fetchedFile = repository.getFile(file.id)

        assertTrue(isDeleted)
        assertNull(fetchedFile)
    }

    @Test
    fun getAllFiles_should_return_all_inserted_files() = runTest {
        val files = listOf(createTestFile(), createTestFile())
        files.forEach { repository.addFile(it) }

        val result = repository.getAllFiles()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(files))
    }

    @Test
    fun getFilesByUser_should_filter_by_uploadedBy() = runTest {
        val userId = UUID.randomUUID()
        val file1 = createTestFile(uploadedBy = userId)
        val file2 = createTestFile(uploadedBy = userId)
        repository.addFile(file1)
        repository.addFile(file2)

        val result = repository.getFilesByUser(userId)

        assertEquals(2, result.size)
    }

    // Отрицательные тесты

    @Test
    fun getFile_should_return_null_for_non_existent_id() = runTest {
        val result = repository.getFile(UUID.randomUUID())

        assertNull(result)
    }

    @Test
    fun updateFile_should_return_false_for_non_existent_file() = runTest {
        val isUpdated = repository.updateFile(createTestFile())

        assertFalse(isUpdated)
    }

    @Test
    fun deleteFile_should_return_false_for_non_existent_id() = runTest {
        val isDeleted = repository.deleteFile(UUID.randomUUID())

        assertFalse(isDeleted)
    }

    @Test
    fun addFile_should_throw_exception_on_conflict() = runTest {
        val file = createTestFile()
        repository.addFile(file)

        assertThrows(Exception::class.java) {
            runTest { repository.addFile(file) }
        }
    }

    private fun createTestFile(
        id: UUID = UUID.randomUUID(),
        name: String = "document.pdf",
        type: String? = "PDF",
        path: String = "/uploads/docs",
        uploadedBy: UUID = UUID.randomUUID(),
        uploadedTimestamp: Instant = Instant.now()
    ): File {
        return File(
            id = id,
            name = name,
            type = type,
            path = path,
            uploadedBy = uploadedBy,
            uploadedTimestamp = uploadedTimestamp
        )
    }
}
