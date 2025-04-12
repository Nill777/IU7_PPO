package com.distributed_messenger.test.unit.services

import com.distributed_messenger.domain.models.User
import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.domain.repositories.IUserRepository
import com.distributed_messenger.implementation.services.UserService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserServiceTest {
    private lateinit var userService: UserService
    private val mockUserRepository = mockk<IUserRepository>()

    @BeforeEach
    fun setup() {
        userService = UserService(mockUserRepository)
    }

    @Test
    fun `register should return new user id`() = runTest {
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.addUser(any()) } returns userId

        val result = userService.register("testUser", UserRole.USER)

        assertEquals(userId, result)
        coVerify { mockUserRepository.addUser(match {
            it.username == "testUser" && it.role == UserRole.USER
        }) }
    }

    @Test
    fun `register with role should use specified role`() = runTest {
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.addUser(any()) } returns userId

        val result = userService.register("admin", UserRole.ADMINISTRATOR)

        assertEquals(userId, result)
        coVerify { mockUserRepository.addUser(match {
            it.username == "admin" && it.role == UserRole.ADMINISTRATOR
        }) }
    }

    @Test
    fun `getUser should return user when exists`() = runTest {
        val userId = UUID.randomUUID()
        val testUser = User(userId, "testUser", UserRole.USER, null, UUID.randomUUID(), UUID.randomUUID())
        coEvery { mockUserRepository.getUser(userId) } returns testUser

        val result = userService.getUser(userId)

        assertEquals(testUser, result)
    }

    @Test
    fun `getAllUsers should return all users`() = runTest {
        val testUsers = listOf(
            User(UUID.randomUUID(), "user1", UserRole.USER, null, UUID.randomUUID(), UUID.randomUUID()),
            User(UUID.randomUUID(), "user2", UserRole.ADMINISTRATOR, null, UUID.randomUUID(), UUID.randomUUID())
        )
        coEvery { mockUserRepository.getAllUsers() } returns testUsers

        val result = userService.getAllUsers()

        assertEquals(testUsers, result)
    }

    @Test
    fun `updateUser should return true on success`() = runTest {
        val userId = UUID.randomUUID()
        val original = User(userId, "oldName", UserRole.USER, null, UUID.randomUUID(), UUID.randomUUID())
        coEvery { mockUserRepository.getUser(userId) } returns original
        coEvery { mockUserRepository.updateUser(any()) } returns true

        val result = userService.updateUser(userId, "newName")

        assertTrue(result)
        coVerify {
            mockUserRepository.updateUser(match { user ->
                user.id == userId && user.username == "newName"
            })
        }
    }

    @Test
    fun `updateUser should return false when user not found`() = runTest {
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.getUser(userId) } returns null

        val result = userService.updateUser(userId, "newName")

        assertFalse(result)
        coVerify(exactly = 0) { mockUserRepository.updateUser(any()) }
    }

    @Test
    fun `deleteUser should return true when successful`() = runTest {
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.deleteUser(userId) } returns true

        val result = userService.deleteUser(userId)

        assertTrue(result)
    }
}