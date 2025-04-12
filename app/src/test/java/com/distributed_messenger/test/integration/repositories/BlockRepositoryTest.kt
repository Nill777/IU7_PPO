package com.distributed_messenger.test.repositories

import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.entities.BlockEntity
import com.distributed_messenger.domain.models.Block
import com.distributed_messenger.implementation.repositories.BlockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlockRepositoryTest {

    private lateinit var mockDao: BlockDao
    private lateinit var repository: BlockRepository

    @Before
    fun setup() {
        mockDao = mockk(relaxed = true)
        repository = BlockRepository(mockDao)
    }

    // region Положительные тесты

    @Test
    fun `getBlock should return block when exists`() = runTest {
        // Arrange
        val testEntity = BlockEntity(1, 1, 2, "spam", "123")
        coEvery { mockDao.getBlockById(1) } returns testEntity

        // Act
        val result = repository.getBlock(1)

        // Assert
        assertEquals(testEntity.toDomain(), result)
        coVerify { mockDao.getBlockById(1) }
    }

    @Test
    fun `getAllBlocks should return all blocks`() = runTest {
        // Arrange
        val testEntities = listOf(
            BlockEntity(1, 1, 2, "spam", "123"),
            BlockEntity(2, 3, 4, null, "124")
        )
        coEvery { mockDao.getAllBlocks() } returns testEntities

        // Act
        val result = repository.getAllBlocks()

        // Assert
        assertEquals(2, result.size)
        coVerify { mockDao.getAllBlocks() }
    }

    @Test
    fun `getBlocksByUser should return user blocks`() = runTest {
        // Arrange
        val testEntities = listOf(
            BlockEntity(1, 1, 2, "spam", "123"),
            BlockEntity(2, 1, 3, "harassment", "124")
        )
        coEvery { mockDao.getBlocksByUserId(1) } returns testEntities

        // Act
        val result = repository.getBlocksByUser(1)

        // Assert
        assertEquals(2, result.size)
        coVerify { mockDao.getBlocksByUserId(1) }
    }

    @Test
    fun `addBlock should return generated ID`() = runTest {
        // Arrange
        val testBlock = Block(0, 1, 2, "spam", "123")
        coEvery { mockDao.insertBlock(any()) } returns 42L

        // Act
        val result = repository.addBlock(testBlock)

        // Assert
        assertEquals(42, result)
        coVerify { mockDao.insertBlock(match { it.blockerId == 1 }) }
    }

    @Test
    fun `updateBlock should return true when successful`() = runTest {
        // Arrange
        val testBlock = Block(1, 1, 2, "spam", "123")
        coEvery { mockDao.updateBlock(any()) } returns 1

        // Act
        val result = repository.updateBlock(testBlock.id, testBlock)

        // Assert
        assertTrue(result)
        coVerify { mockDao.updateBlock(match { it.id == 1 }) }
    }

    @Test
    fun `deleteBlock should return true when deleted`() = runTest {
        // Arrange
        coEvery { mockDao.deleteBlock(1) } returns 1

        // Act
        val result = repository.deleteBlock(1)

        // Assert
        assertTrue(result)
        coVerify { mockDao.deleteBlock(1) }
    }

    // endregion

    // region Отрицательные тесты

    @Test
    fun `getBlock should return null when not exists`() = runTest {
        // Arrange
        coEvery { mockDao.getBlockById(1) } returns null

        // Act
        val result = repository.getBlock(1)

        // Assert
        assertNull(result)
    }

    @Test
    fun `updateBlock should return false when not updated`() = runTest {
        // Arrange
        val testBlock = Block(1, 1, 2, "spam", "123")
        coEvery { mockDao.updateBlock(any()) } returns 0

        // Act
        val result = repository.updateBlock(testBlock.id, testBlock)

        // Assert
        assertTrue(!result)
    }

    @Test
    fun `deleteBlock should return false when not deleted`() = runTest {
        // Arrange
        coEvery { mockDao.deleteBlock(1) } returns 0

        // Act
        val result = repository.deleteBlock(1)

        // Assert
        assertTrue(!result)
    }

    // endregion
}

// Extension-функции для конвертации
private fun BlockEntity.toDomain() = Block(
    id = id,
    blockerId = blockerId,
    blockedUserId = blockedUserId,
    reason = reason,
    timestamp = timestamp
)

private fun Block.toEntity() = BlockEntity(
    id = id,
    blockerId = blockerId,
    blockedUserId = blockedUserId,
    reason = reason,
    timestamp = timestamp
)