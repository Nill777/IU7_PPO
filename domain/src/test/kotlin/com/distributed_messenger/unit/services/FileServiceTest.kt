package com.distributed_messenger.unit.services

import com.distributed_messenger.core.File
import com.distributed_messenger.domain.irepositories.IFileRepository
import com.distributed_messenger.domain.services.FileService
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
        val fileId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        coEvery { mockFileRepository.addFile(any()) } returns fileId

        val result = fileService.uploadFile("test.txt", "text", "/path", userId)

        assertEquals(fileId, result)
        coVerify { mockFileRepository.addFile(match {
            it.name == "test.txt" && it.type == "text" && it.path == "/path" && it.uploadedBy == userId
        }) }
    }

    @Test
    fun `getFile should return file when exists`() = runTest {
        val fileId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val testFile = File(fileId, "test.txt", "text", "/path", userId, Instant.now())
        coEvery { mockFileRepository.getFile(fileId) } returns testFile

        val result = fileService.getFile(fileId)

        assertEquals(testFile, result)
    }

    @Test
    fun `getUserFiles should return files for user`() = runTest {
        val userId = UUID.randomUUID()
        val testFiles = listOf(
            File(UUID.randomUUID(), "file1.txt", "text", "/path", userId, Instant.now()),
            File(UUID.randomUUID(), "file2.txt", "image", "/path", userId, Instant.now())
        )
        coEvery { mockFileRepository.getFilesByUser(userId) } returns testFiles

        val result = fileService.getUserFiles(userId)

        assertEquals(testFiles, result)
    }

    @Test
    fun `deleteFile should return true when successful`() = runTest {
        val fileId = UUID.randomUUID()
        coEvery { mockFileRepository.deleteFile(fileId) } returns true

        val result = fileService.deleteFile(fileId)

        assertTrue(result)
    }
}
