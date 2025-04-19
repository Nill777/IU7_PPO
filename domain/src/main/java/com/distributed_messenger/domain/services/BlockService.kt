package com.distributed_messenger.domain.services

import com.distributed_messenger.core.Block
import com.distributed_messenger.domain.iservices.IBlockService
import com.distributed_messenger.domain.irepositories.IBlockRepository
import java.util.UUID
import java.time.Instant

class BlockService(private val blockRepository: IBlockRepository) : IBlockService {
    override suspend fun blockUser(blockerId: UUID, blockedUserId: UUID, reason: String?): UUID {
        val block = Block(
            id = UUID.randomUUID(),
            blockerId = blockerId,
            blockedUserId = blockedUserId,
            reason = reason,
            timestamp = Instant.now()
        )
        return blockRepository.addBlock(block)
    }

    override suspend fun getBlock(id: UUID): Block? {
        return blockRepository.getBlock(id)
    }

    override suspend fun getUserBlocks(userId: UUID): List<Block> {
        return blockRepository.getBlocksByUser(userId)
    }

    override suspend fun unblockUser(id: UUID): Boolean {
        return blockRepository.deleteBlock(id)
    }
}
