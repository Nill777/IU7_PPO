package com.distributed_messenger.domain.controllers

import com.distributed_messenger.domain.services.IBlockService
import java.util.UUID

class BlockController(private val blockService: IBlockService) {
    suspend fun blockUser(
        blockerId: UUID,
        blockedUserId: UUID,
        reason: String? = null
    ) = blockService.blockUser(blockerId, blockedUserId, reason)

    suspend fun getBlock(id: UUID) = blockService.getBlock(id)

    suspend fun getUserBlocks(userId: UUID) =
        blockService.getUserBlocks(userId)

    suspend fun unblockUser(id: UUID) =
        blockService.unblockUser(id)
}