package com.distributed_messenger.implementation.repositories

import com.distributed_messenger.domain.models.Block
import com.distributed_messenger.domain.repositories.IBlockRepository
import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.entities.BlockEntity
import java.util.UUID

class BlockRepository(private val blockDao: BlockDao) : IBlockRepository {
    override suspend fun getBlock(id: UUID): Block? {
        return blockDao.getBlockById(id)?.toDomain()
    }

    override suspend fun getAllBlocks(): List<Block> {
        return blockDao.getAllBlocks().map { it.toDomain() }
    }

    override suspend fun getBlocksByUser(userId: UUID): List<Block> {
        return blockDao.getBlocksByUserId(userId).map { it.toDomain() }
    }

    override suspend fun addBlock(block: Block): UUID {
        return blockDao.insertBlock(block.toEntity())
    }

    override suspend fun updateBlock(block: Block): Boolean {
        return blockDao.updateBlock(block.toEntity()) > 0
    }

    override suspend fun deleteBlock(id: UUID): Boolean {
        return blockDao.deleteBlock(id) > 0
    }
    private fun Block.toEntity(): BlockEntity {
        return BlockEntity(
            id = id,
            blockerId = blockerId,
            blockedUserId = blockedUserId,
            reason = reason,
            timestamp = timestamp
        )
    }

    private fun BlockEntity.toDomain(): Block {
        return Block(
            id = id,
            blockerId = blockerId,
            blockedUserId = blockedUserId,
            reason = reason,
            timestamp = timestamp
        )
    }
}