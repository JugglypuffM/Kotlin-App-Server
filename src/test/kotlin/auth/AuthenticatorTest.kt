package auth

import database.DatabaseMock
import domain.User
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

        assertTrue(result.success)
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

        assertFalse(result.success)
        assertEquals("User already exists.", result.message)
        assertEquals(user1, database.get("johndoe").get())
    }

    @Test
    fun `should login user successfully`() {
        val user = User(name = "John Doe", login = "johndoe", password = "password123")

        authenticator.register(user)
        val result = authenticator.login("johndoe", "password123")

        assertTrue(result.success)
        assertEquals("User successfully logged in.", result.message)
    }

    @Test
    fun `should not login with incorrect password`() {
        val user = User(name = "John Doe", login = "johndoe", password = "password123")

        authenticator.register(user)
        val result = authenticator.login("johndoe", "wrongpassword")

        assertFalse(result.success)
        assertEquals("Invalid login or password.", result.message)
    }

    @Test
    fun `should not login non-existing user`() {
        val result = authenticator.login("nonexisting", "somepassword")

        assertFalse(result.success)
        assertEquals("Invalid login or password.", result.message)
    }
}
