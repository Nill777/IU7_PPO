package com.distributed_messenger.domain.repositories

import com.distributed_messenger.domain.models.Block
import java.util.UUID

interface IBlockRepository {
    suspend fun getBlock(id: UUID): Block?
    suspend fun getAllBlocks(): List<Block>
    suspend fun getBlocksByUser(userId: UUID): List<Block>
    suspend fun addBlock(block: Block): UUID
    suspend fun updateBlock(block: Block): Boolean
    suspend fun deleteBlock(id: UUID): Boolean
}
