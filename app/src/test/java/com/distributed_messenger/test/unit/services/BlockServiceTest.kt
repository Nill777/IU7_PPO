package com.distributed_messenger.test.unit.services

import com.distributed_messenger.domain.models.Block
import com.distributed_messenger.domain.repositories.IBlockRepository
import com.distributed_messenger.implementation.services.BlockService
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

class BlockServiceTest {
    private lateinit var blockService: BlockService
    private val mockBlockRepository = mockk<IBlockRepository>()

    @BeforeEach
    fun setup() {
        blockService = BlockService(mockBlockRepository)
    }

    @Test
    fun `blockUser should return block id`() = runTest {
        // Arrange
        val blockerId = UUID.randomUUID()
        val blockedUserId = UUID.randomUUID()
        val reason = "spam"
        val blockId = UUID.randomUUID()
        coEvery { mockBlockRepository.addBlock(any()) } returns blockId

        // Act
        val result = blockService.blockUser(blockerId, blockedUserId, reason)

        // Assert
        assertEquals(blockId, result)
        coVerify { mockBlockRepository.addBlock(match {
            it.blockerId == blockerId && it.blockedUserId == blockedUserId && it.reason == reason
        }) }
    }

    @Test
    fun `blockUser without reason should work`() = runTest {
        // Arrange
        val blockerId = UUID.randomUUID()
        val blockedUserId = UUID.randomUUID()
        val blockId = UUID.randomUUID()
        coEvery { mockBlockRepository.addBlock(any()) } returns blockId

        // Act
        val result = blockService.blockUser(blockerId, blockedUserId)

        // Assert
        assertEquals(blockId, result)
        coVerify { mockBlockRepository.addBlock(match {
            it.blockerId == blockerId && it.blockedUserId == blockedUserId && it.reason == null
        }) }
    }

    @Test
    fun `getBlock should return block when exists`() = runTest {
        // Arrange
        val blockId = UUID.randomUUID()
        val testBlock = Block(blockId, UUID.randomUUID(), UUID.randomUUID(), "spam", Instant.now())
        coEvery { mockBlockRepository.getBlock(blockId) } returns testBlock

        // Act
        val result = blockService.getBlock(blockId)

        // Assert
        assertEquals(testBlock, result)
    }

    @Test
    fun `getUserBlocks should return blocks for user`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        val testBlocks = listOf(
            Block(UUID.randomUUID(), userId, UUID.randomUUID(), "spam", Instant.now()),
            Block(UUID.randomUUID(), userId, UUID.randomUUID(), "harassment", Instant.now())
        )
        coEvery { mockBlockRepository.getBlocksByUser(userId) } returns testBlocks

        // Act
        val result = blockService.getUserBlocks(userId)

        // Assert
        assertEquals(testBlocks, result)
    }

    @Test
    fun `unblockUser should return true when successful`() = runTest {
        // Arrange
        val blockId = UUID.randomUUID()
        coEvery { mockBlockRepository.deleteBlock(blockId) } returns true

        // Act
        val result = blockService.unblockUser(blockId)

        // Assert
        assertTrue(result)
    }
}
