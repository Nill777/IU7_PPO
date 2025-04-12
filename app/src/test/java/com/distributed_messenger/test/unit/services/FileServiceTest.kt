package com.distributed_messenger.test.unit.services

import com.distributed_messenger.domain.models.File
import com.distributed_messenger.domain.repositories.IFileRepository
import com.distributed_messenger.implementation.services.FileService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileServiceTest {
    private lateinit var fileService: FileService
    private val mockFileRepository = mockk<IFileRepository>()

    @BeforeEach
    fun setup() {
        fileService = FileService(mockFileRepository)
    }

    @Test
    fun `uploadFile should return file id`() = runTest {
        // Arrange
        val fileId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        coEvery { mockFileRepository.addFile(any()) } returns fileId

        // Act
        val result = fileService.uploadFile("test.txt", "text", "/path", userId)

        // Assert
        assertEquals(fileId, result)
        coVerify { mockFileRepository.addFile(match {
            it.name == "test.txt" && it.type == "text" && it.path == "/path" && it.uploadedBy == userId
        }) }
    }

    @Test
    fun `getFile should return file when exists`() = runTest {
        // Arrange
        val fileId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val testFile = File(fileId, "test.txt", "text", "/path", userId, Instant.now())
        coEvery { mockFileRepository.getFile(fileId) } returns testFile

        // Act
        val result = fileService.getFile(fileId)

        // Assert
        assertEquals(testFile, result)
    }

    @Test
    fun `getUserFiles should return files for user`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        val testFiles = listOf(
            File(UUID.randomUUID(), "file1.txt", "text", "/path", userId, Instant.now()),
            File(UUID.randomUUID(), "file2.txt", "image", "/path", userId, Instant.now())
        )
        coEvery { mockFileRepository.getFilesByUser(userId) } returns testFiles

        // Act
        val result = fileService.getUserFiles(userId)

        // Assert
        assertEquals(testFiles, result)
    }

    @Test
    fun `deleteFile should return true when successful`() = runTest {
        // Arrange
        val fileId = UUID.randomUUID()
        coEvery { mockFileRepository.deleteFile(fileId) } returns true

        // Act
        val result = fileService.deleteFile(fileId)

        // Assert
        assertTrue(result)
    }
}
