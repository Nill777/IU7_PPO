package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.Block
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.distributed_messenger.domain.irepositories.IBlockRepository
import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.entities.BlockEntity
import java.util.UUID

class BlockRepository(private val blockDao: BlockDao) : IBlockRepository {
    private val loggerWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "BlockRepository"
    )

    override suspend fun getBlock(id: UUID): Block? =
        loggerWrapper {
            blockDao.getBlockById(id)?.toDomain()
        }

    override suspend fun getAllBlocks(): List<Block> =
        loggerWrapper {
            blockDao.getAllBlocks().map { it.toDomain() }
        }

    override suspend fun getBlocksByUser(userId: UUID): List<Block> =
        loggerWrapper {
            blockDao.getBlocksByUserId(userId).map { it.toDomain() }
        }

    override suspend fun addBlock(block: Block): UUID =
        loggerWrapper {
            val rowId = blockDao.insertBlock(block.toEntity())
            if (rowId == -1L) {
                throw Exception("Failed to insert block")
            }
            block.id
        }

    override suspend fun updateBlock(block: Block): Boolean =
        loggerWrapper {
            blockDao.updateBlock(block.toEntity()) > 0
        }

    override suspend fun deleteBlock(id: UUID): Boolean =
        loggerWrapper {
            blockDao.deleteBlock(id) > 0
        }

    override suspend fun deleteBlocksByUserId(blockerId: UUID, blockedUserId: UUID): Boolean =
        loggerWrapper {
            blockDao.deleteBlocksByUserId(blockerId, blockedUserId) > 0
        }

    private fun Block.toEntity(): BlockEntity {
        return BlockEntity(
            blockId = id,
            blockerId = blockerId,
            blockedUserId = blockedUserId,
            reason = reason,
            blockTimestamp = timestamp
        )
    }

    private fun BlockEntity.toDomain(): Block {
        return Block(
            id = blockId,
            blockerId = blockerId,
            blockedUserId = blockedUserId,
            reason = reason,
            timestamp = blockTimestamp
        )
    }
}