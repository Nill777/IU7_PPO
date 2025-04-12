package com.distributed_messenger.integration.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.domain.models.Block
import com.distributed_messenger.implementation.repositories.BlockRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

/*
Положительные тесты:
    Добавление блока: Проверяет, что блок сохраняется и возвращается корректный ID.
    Обновление блока: Меняет причину блокировки и проверяет обновление в БД.
    Удаление блока: Убеждается, что блок удаляется из БД.
    Получение всех блоков: Проверяет, что возвращаются все добавленные блоки.
    Фильтрация по пользователю: Проверяет, что возвращаются только блоки, связанные с указанным пользователем.
Отрицательные тесты:
    Получение несуществующего блока: Возвращает null.
    Обновление несуществующего блока: Возвращает false.
    Удаление несуществующего блока: Возвращает false.
    Конфликт при добавлении: Проверяет обработку дубликатов (если в DAO включены ограничения).
 */

class BlockRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: BlockRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = BlockRepository(database.blockDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Положительные тесты

    @Test
    fun addBlock_should_insert_block_and_return_id() = runTest {
        val block = createTestBlock()

        val insertedId = repository.addBlock(block)
        val fetchedBlock = repository.getBlock(insertedId)

        assertEquals(block.id, insertedId)
        assertEquals(block, fetchedBlock)
    }

    @Test
    fun updateBlock_should_modify_existing_block() = runTest {
        val block = createTestBlock().apply { repository.addBlock(this) }
        val updatedBlock = block.copy(reason = "harassment")

        val isUpdated = repository.updateBlock(updatedBlock)
        val fetchedBlock = repository.getBlock(block.id)

        assertTrue(isUpdated)
        assertEquals("harassment", fetchedBlock?.reason)
    }

    @Test
    fun deleteBlock_should_remove_block_from_db() = runTest {
        val block = createTestBlock().apply { repository.addBlock(this) }

        val isDeleted = repository.deleteBlock(block.id)
        val fetchedBlock = repository.getBlock(block.id)

        assertTrue(isDeleted)
        assertNull(fetchedBlock)
    }

    @Test
    fun getAllBlocks_should_return_all_inserted_blocks() = runTest {
        val blocks = listOf(createTestBlock(), createTestBlock())
        blocks.forEach { repository.addBlock(it) }

        val result = repository.getAllBlocks()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(blocks))
    }

    @Test
    fun getBlocksByUser_should_filter_by_blockerId() = runTest {
        val userId = UUID.randomUUID()
        val block1 = createTestBlock(blockerId = userId)
        val block2 = createTestBlock(blockerId = userId)
        repository.addBlock(block1)
        repository.addBlock(block2)

        val result = repository.getBlocksByUser(userId)

        assertEquals(2, result.size)
    }

    // Отрицательные тесты

    @Test
    fun getBlock_should_return_null_for_non_existent_id() = runTest {
        val result = repository.getBlock(UUID.randomUUID())

        assertNull(result)
    }

    @Test
    fun updateBlock_should_return_false_for_non_existent_id() = runTest {
        val isUpdated = repository.updateBlock(createTestBlock())

        assertFalse(isUpdated)
    }

    @Test
    fun deleteBlock_should_return_false_for_non_existent_id() = runTest {
        val isDeleted = repository.deleteBlock(UUID.randomUUID())

        assertFalse(isDeleted)
    }

    @Test
    fun addBlock_should_throw_exception_on_conflict() = runTest {
        val block = createTestBlock()
        repository.addBlock(block)

        assertThrows(Exception::class.java) {
            runTest { repository.addBlock(block) }
        }
    }

    private fun createTestBlock(
        id: UUID = UUID.randomUUID(),
        blockerId: UUID = UUID.randomUUID(),
        reason: String? = "spam"
    ): Block {
        return Block(
            id = id,
            blockerId = blockerId,
            blockedUserId = UUID.randomUUID(),
            reason = reason,
            timestamp = Instant.now()
        )
    }
}
