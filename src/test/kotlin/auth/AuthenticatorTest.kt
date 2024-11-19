package auth

import database.dao.DAO.DatabaseException
import database.manager.DatabaseManager
import domain.Account
import domain.ResultCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class AuthenticatorTest {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var authenticator: Authenticator

    @BeforeEach
    fun setUp() {
        databaseManager = mockk()
        authenticator = Authenticator(databaseManager)
    }

    @Test
    fun `should register user successfully`() {
        val account = Account(login = "c", password = "password123")

        every { databaseManager.addAccount(account) } returns Unit

        val result = authenticator.register(account)

        assertEquals(ResultCode.OPERATION_SUCCESS, result.resultCode)
        assertEquals("User successfully registered.", result.message)
    }

    @Test
    fun `should not register user with existing login`() {
        val account = Account(login = "johndoe", password = "password123")

        every { databaseManager.addAccount(account) } throws DatabaseException("")

        val result = authenticator.register(account)

        assertEquals(ResultCode.USER_ALREADY_EXISTS, result.resultCode)
        assertEquals("User already exists.", result.message)
    }

    @Test
    fun `should login user successfully`() {
        val account = Account(login = "johndoe", password = "password123")

        every { databaseManager.getAccount(account.login) } returns Optional.of(account)

        val result = authenticator.login("johndoe", "password123")

        assertEquals(ResultCode.OPERATION_SUCCESS, result.resultCode)
        assertEquals("User successfully logged in.", result.message)
    }

    @Test
    fun `should not login with incorrect password`() {
        val account = Account(login = "johndoe", password = "password123")

        every { databaseManager.getAccount(account.login) } returns Optional.of(account)

        val result = authenticator.login("johndoe", "wrongpassword")

        assertEquals(ResultCode.INVALID_CREDENTIALS, result.resultCode)
        assertEquals("Wrong login or password.", result.message)
    }

    @Test
    fun `should not login non-existing user`() {
        val account = Account(login = "nonexisting", password = "somepassword")

        every { databaseManager.getAccount(account.login) } returns Optional.empty<Account>()

        val result = authenticator.login("nonexisting", "somepassword")

        assertEquals(ResultCode.INVALID_CREDENTIALS, result.resultCode)
        assertEquals("Wrong login or password.", result.message)
    }
}
