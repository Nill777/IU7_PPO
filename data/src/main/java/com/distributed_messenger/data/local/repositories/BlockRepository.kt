package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.Block
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.core.logging.LoggingWrapper
import com.distributed_messenger.domain.irepositories.IBlockRepository
import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.entities.BlockEntity
import java.util.UUID

class BlockRepository(private val blockDao: BlockDao) : IBlockRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "BlockRepository"
    )

    override suspend fun getBlock(id: UUID): Block? =
        loggingWrapper {
            blockDao.getBlockById(id)?.toDomain()
        }

    override suspend fun getAllBlocks(): List<Block> =
        loggingWrapper {
            blockDao.getAllBlocks().map { it.toDomain() }
        }

    override suspend fun getBlocksByUser(userId: UUID): List<Block> =
        loggingWrapper {
            blockDao.getBlocksByUserId(userId).map { it.toDomain() }
        }

    override suspend fun addBlock(block: Block): UUID =
        loggingWrapper {
            val rowId = blockDao.insertBlock(block.toEntity())
            if (rowId == -1L) {
                throw Exception("Failed to insert block")
            }
            block.id
        }

    override suspend fun updateBlock(block: Block): Boolean =
        loggingWrapper {
            blockDao.updateBlock(block.toEntity()) > 0
        }

    override suspend fun deleteBlock(id: UUID): Boolean =
        loggingWrapper {
            blockDao.deleteBlock(id) > 0
        }

    override suspend fun deleteBlocksByUserId(blockerId: UUID, blockedUserId: UUID): Boolean =
        loggingWrapper {
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