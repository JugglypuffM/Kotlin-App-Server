package auth

import database.DbTableMock
import domain.Account
import domain.ResultCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticatorTest {

    private lateinit var database: DbTableMock
    private lateinit var authenticator: Authenticator

    @BeforeEach
    fun setUp() {
        database = DbTableMock()
        authenticator = Authenticator(database)
    }

    @Test
    fun `should register user successfully`() {
        val account = Account(login = "johndoe", password = "password123")

        val result = authenticator.register(account)

        assertEquals(ResultCode.OPERATION_SUCCESS, result.resultCode)
        assertEquals("User successfully registered.", result.message)
        assertTrue(database.get("johndoe").isPresent)
        assertEquals(account, database.get("johndoe").get())
    }

    @Test
    fun `should not register user with existing login`() {
        val account1 = Account(login = "johndoe", password = "password123")
        val account2 = Account(login = "johndoe", password = "password456")

        authenticator.register(account1)
        val result = authenticator.register(account2)

        assertEquals(ResultCode.USER_ALREADY_EXISTS, result.resultCode)
        assertEquals("User already exists.", result.message)
        assertEquals(account1, database.get("johndoe").get())
    }

    @Test
    fun `should login user successfully`() {
        val account = Account(login = "johndoe", password = "password123")

        authenticator.register(account)
        val result = authenticator.login("johndoe", "password123")

        assertEquals(ResultCode.OPERATION_SUCCESS, result.resultCode)
        assertEquals("User successfully logged in.", result.message)
    }

    @Test
    fun `should not login with incorrect password`() {
        val account = Account(login = "johndoe", password = "password123")

        authenticator.register(account)
        val result = authenticator.login("johndoe", "wrongpassword")

        assertEquals(ResultCode.INVALID_CREDENTIALS, result.resultCode)
        assertEquals("Wrong login or password.", result.message)
    }

    @Test
    fun `should not login non-existing user`() {
        val result = authenticator.login("nonexisting", "somepassword")

        assertEquals(ResultCode.INVALID_CREDENTIALS, result.resultCode)
        assertEquals("Wrong login or password.", result.message)
    }
}
