package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.Block
import com.distributed_messenger.domain.irepositories.IBlockRepository
import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.entities.BlockEntity
import java.util.UUID

class BlockRepository(private val blockDao: BlockDao) :
    com.distributed_messenger.domain.irepositories.IBlockRepository {
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
        val rowId = blockDao.insertBlock(block.toEntity())
        if (rowId == -1L) {
            throw Exception("Failed to insert block")
        }
        return block.id
    }

    override suspend fun updateBlock(block: Block): Boolean {
        return blockDao.updateBlock(block.toEntity()) > 0
    }

    override suspend fun deleteBlock(id: UUID): Boolean {
        return blockDao.deleteBlock(id) > 0
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