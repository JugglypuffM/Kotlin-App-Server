package auth

import database.DatabaseMock
import domain.User
import domain.ResultCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticatorTest {

    private lateinit var database: DatabaseMock
    private lateinit var authenticator: Authenticator

    @BeforeEach
    fun setUp() {
        database = DatabaseMock()
        authenticator = Authenticator(database)
    }

    @Test
    fun `should register user successfully`() {
        val user = User(name = "John Doe", login = "johndoe", password = "password123")

        val result = authenticator.register(user)

        assertEquals(ResultCode.OPERATION_SUCCESS, result.resultCode)
        assertEquals("User successfully registered.", result.message)
        assertTrue(database.get("johndoe").isPresent)
        assertEquals(user, database.get("johndoe").get())
    }

    @Test
    fun `should not register user with existing login`() {
        val user1 = User(name = "John Doe", login = "johndoe", password = "password123")
        val user2 = User(name = "Jane Doe", login = "johndoe", password = "password456")

        authenticator.register(user1)
        val result = authenticator.register(user2)

        assertEquals(ResultCode.USER_ALREADY_EXISTS, result.resultCode)
        assertEquals("User already exists.", result.message)
        assertEquals(user1, database.get("johndoe").get())
    }

    @Test
    fun `should login user successfully`() {
        val user = User(name = "John Doe", login = "johndoe", password = "password123")

        authenticator.register(user)
        val result = authenticator.login("johndoe", "password123")

        assertEquals(ResultCode.OPERATION_SUCCESS, result.resultCode)
        assertEquals("User successfully logged in.", result.message)
    }

    @Test
    fun `should not login with incorrect password`() {
        val user = User(name = "John Doe", login = "johndoe", password = "password123")

        authenticator.register(user)
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
