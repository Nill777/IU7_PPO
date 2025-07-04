package com.distributed_messenger.domain.iservices

import com.distributed_messenger.core.Block
import java.util.UUID

interface IBlockService {
    suspend fun blockUser(blockerId: UUID, blockedUserId: UUID, reason: String? = null): UUID
    suspend fun getBlock(id: UUID): Block?
    suspend fun getUserBlocks(userId: UUID): List<Block>
    suspend fun deleteBlock(id: UUID): Boolean
    suspend fun unblockUser(blockerId: UUID, blockedUserId: UUID): Boolean
}
